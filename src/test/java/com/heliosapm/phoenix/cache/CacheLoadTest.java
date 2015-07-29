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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.management.ObjectName;
import javax.xml.bind.DatatypeConverter;

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
//	public static final String JDBC_URL = "jdbc:h2:tcp://10.5.202.22:8083//var/opt/opentsdb/sqlcatalog/tsdb/tsdb";
	public static final String JDBC_URL = "jdbc:h2:tcp://127.0.0.1:9092/tsdb";
	
	public static final String FILE_NAME = System.getProperty("java.io.tmpdir") + File.separator + "tsmeta.db";
	
	public static final String TAGK_SQL = "SELECT NAME FROM TSD_TAGK WHERE XUID = ?";
	public static final String TAGV_SQL = "SELECT NAME FROM TSD_TAGV WHERE XUID = ?";
	public static final String METRIC_SQL = "SELECT NAME FROM TSD_METRIC WHERE XUID = ?";
	
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rset = null;
	
	/**
	 * Creates a new CacheLoadTest
	 */
	public CacheLoadTest() {
		initDb();
	}
	
	public CachedUIDMeta getName(final Connection conn, final UniqueId.UniqueIdType type, final String xuid) {
		switch(type) {
		case METRIC:
			return getMetric(conn, xuid);
		case TAGK:
			return getTagK(conn, xuid);
		case TAGV:
			return getTagV(conn, xuid);
		default:
			throw new RuntimeException();
			
		}
	}
	
	final CacheImpl cache = CacheImpl.getInstance(FILE_NAME);
	
	
	public CachedUIDMeta getTagK(final Connection conn, final String xuid) {
		final UniqueId.UniqueIdType _type = UniqueId.UniqueIdType.TAGK;
		CachedUIDMeta cm = cache.getCachedUIDMeta(_type, xuid);
		if(cm==null) {			
			cm = new CachedUIDMeta(getName(conn, TAGK_SQL, xuid), UniqueId.stringToUid(xuid), _type);
			cache.putCachedUIDMeta(_type, cm);
		}
		return cm;
	}
	
	public CachedUIDMeta getTagV(final Connection conn, final String xuid) {
		final UniqueId.UniqueIdType _type = UniqueId.UniqueIdType.TAGV;
		CachedUIDMeta cm = cache.getCachedUIDMeta(_type, xuid);
		if(cm==null) {			
			cm = new CachedUIDMeta(getName(conn, TAGV_SQL, xuid), UniqueId.stringToUid(xuid), _type);
			cache.putCachedUIDMeta(_type, cm);
		}
		return cm;
	}
	
	public CachedUIDMeta getMetric(final Connection conn, final String xuid) {
		final UniqueId.UniqueIdType _type = UniqueId.UniqueIdType.METRIC;
		CachedUIDMeta cm = cache.getCachedUIDMeta(_type, xuid);
		if(cm==null) {			
			cm = new CachedUIDMeta(getName(conn, METRIC_SQL, xuid), UniqueId.stringToUid(xuid), _type);
			cache.putCachedUIDMeta(_type, cm);
		}
		return cm;
	}


	
	public String getName(final Connection conn, final String sql, final String xuid) {
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			 ps = conn.prepareStatement(sql);
			 ps.setString(1, xuid);
			 rset = ps.executeQuery();
			 rset.next();
			 return rset.getString(1);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get name for [" + sql.replace("?", xuid) + "]", ex);
		} finally {
			if(rset!=null) try { rset.close(); } catch (Exception x) {/* No Op */}
			if(ps!=null) try { ps.close(); } catch (Exception x) {/* No Op */}
		}
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
				byte[] metricBytes = new byte[3];
				System.arraycopy(bytes, 0, metricBytes, 0, 3);
				List<byte[]> uidBytes = UniqueId.getTagPairsFromTSUID(ctm.getTsuid());
				log.info("Fetching Metas for TS {}", ctm);
				Set<CachedUIDMeta> cmetas = new LinkedHashSet<CachedUIDMeta>(uidBytes.size()+1);
				cmetas.add(getName(conn, UniqueId.UniqueIdType.METRIC, DatatypeConverter.printHexBinary(metricBytes)));
				for(byte[] b: uidBytes) {
					final String pairBytes = DatatypeConverter.printHexBinary(b);
					//log.info("Pairs: [{}]", pairBytes);
					cmetas.add(getName(conn, UniqueId.UniqueIdType.TAGK, pairBytes.substring(0, 6)));
					cmetas.add(getName(conn, UniqueId.UniqueIdType.TAGV, pairBytes.substring(6)));
				}
//				final CachedUIDMeta[] uids = ctm.getUIDMetas().toArray(new CachedUIDMeta[0]);
//				log.info("CachedTSMeta: [{}]", ctm);
//				log.info("UIDs: {}", cmetas.toString());
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
				if(!map.containsKey(ct.getTsuidHex())) {
					throw new RuntimeException("Failed to find key: [" + ct.getTsuidHex() + "]");
				}
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
			clt.load(15000);
			delStore();
			clt.load(15000);
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
