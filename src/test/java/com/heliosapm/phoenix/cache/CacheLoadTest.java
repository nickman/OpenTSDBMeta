/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2015, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package com.heliosapm.phoenix.cache;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import javax.management.ObjectName;

import net.opentsdb.uid.UniqueId;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.TxMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heliosapm.utils.time.SystemClock;
import com.heliosapm.utils.time.SystemClock.ElapsedTime;

/**
 * <p>Title: CacheLoadTest</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.cache.CacheLoadTest</code></p>
 */

public class CacheLoadTest {
	private static final Logger log = LoggerFactory .getLogger(CacheLoadTest.class);
	public static final String JDBC_DRIVER = "org.h2.Driver";
	public static final String JDBC_URL = "jdbc:h2:tcp://10.5.202.22:8083//var/opt/opentsdb/sqlcatalog/tsdb/tsdb";
	public static final String FILE_NAME = System.getProperty("java.io.tmpdir") + File.separator + "tsmeta.db";
	
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rset = null;
	
	/**
	 * Creates a new CacheLoadTest
	 */
	public CacheLoadTest() {
		initDb();
	}
	
	protected void initDb() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(JDBC_URL, "sa", "");
			log.info("Connected to [{}]", conn.getMetaData().getURL());
		} catch (Exception ex) {
			close();
			throw new RuntimeException(ex);
		}
	}
	
	protected void finalize() throws Throwable {
		close();
		log.info("Resources Closed");
		super.finalize();
	}
	
	protected void close() {
		if(rset!=null) try { rset.close(); } catch (Exception x) {/* No Op */}
		if(ps!=null) try { ps.close(); } catch (Exception x) {/* No Op */}
		if(conn!=null) try { conn.close(); } catch (Exception x) {/* No Op */}
	}
	
	protected void hclose() {
		if(rset!=null) try { rset.close(); } catch (Exception x) {/* No Op */}
		if(ps!=null) try { ps.close(); } catch (Exception x) {/* No Op */}
	}
	
	
	protected void load(final int max) {
		TxMaker tx = null;
		DB db = null;
		BTreeMap<String, CachedTSMeta> map = null;
		final Set<CachedTSMeta> ctms = new HashSet<CachedTSMeta>(max);
		
		try {
			tx = CacheImpl.getInstance(FILE_NAME).makeTxMaker();
			db = tx.makeTx();
			log.info("tsmeta exists: {}", db.exists(CacheImpl.TSMETA_NAME));
			map = db.treeMap(CacheImpl.TSMETA_NAME);
			ElapsedTime et = SystemClock.startClock();
			//417/378
			//122/110
			ps = conn.prepareStatement("SELECT FQN, TSUID FROM TSD_TSMETA LIMIT ?");
			ps.setInt(1, max);
			ps.setFetchSize(max);
			rset = ps.executeQuery();
			rset.setFetchSize(max);
			int cnt = 0;
			while(rset.next()) {
				final String fqn = rset.getString(1);
				final String tsuid = rset.getString(2);
				final byte[] bytes = UniqueId.stringToUid(tsuid);
				final ObjectName on = new ObjectName(fqn);
//				log.info("TSMETA:  [{}], tsuid: [{}}", fqn, Arrays.toString(stringToUid(tsuid)));
				
				final CachedTSMeta ctm = new CachedTSMeta(on.getDomain(), new TreeMap<String, String>(on.getKeyPropertyList()), bytes);
				final CachedUIDMeta[] uids = ctm.getUIDMetas().toArray(new CachedUIDMeta[0]);
				log.info("CachedTSMeta: [{}]", ctm);
				log.info("UIDs: {}", Arrays.toString(uids));
				cnt++;
				ctms.add(ctm);
			}
			String summary = et.printAvg("TSMetas", cnt);
			log.info("Retrieved [{}] TSMetas from DB. Elapsed: {}", cnt, summary );
			et = SystemClock.startClock();
			for(CachedTSMeta c: ctms) {
				map.put(c.getTsuidHex(), c);
			}
			summary = et.printAvg("Cache Saves", cnt);

			log.info("Cache Saved [{}] TSMetas to Cache. Elapsed: {}", cnt, summary );
			et = SystemClock.startClock();
			for(CachedTSMeta ct: ctms) {
				CachedTSMeta lookedUp = map.get(ct.getTsuidHex());
				if(!lookedUp.equals(ct)) throw new RuntimeException("Mismatch between cached:\n\t[" + ct + "] and looked up:\n\t[" + lookedUp + "]");
			}			
			summary = et.printAvg("Cache Lookups", cnt);
			log.info("Cache Lookups Elapsed: {}", summary );
			
			map.close(); map = null;
			db.commit();
			db.close();
			db = null;
			tx.close();
		} catch (Exception ex) {
			
			throw new RuntimeException(ex);
		} finally {
			hclose();
			if(map!=null) try { map.close(); } catch (Exception x) {/* No Op */}
			if(db!=null) {
				try { db.commit(); } catch (Exception x) {/* No Op */}
				try { db.close(); } catch (Exception x) {/* No Op */}
			}
			if(tx!=null) try { tx.close(); } catch (Exception x) {/* No Op */}
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		delStore();
		try {
			log.info("Cache Load Test");
			CacheLoadTest clt = new CacheLoadTest();
			clt.load(100);
//			delStore();
//			clt.load(15000);
		} finally {
			bigGc();
			delStore();
		}
	}
	
	private static void bigGc() {
		for(int i = 0; i < 5; i++) {
			System.gc();
		}		
	}
	
	private static void delStore() {
		final boolean del = new File(FILE_NAME).delete();
		log.info("Deleted Store: {}", del);		
	}

}
