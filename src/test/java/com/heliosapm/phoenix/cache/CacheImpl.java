/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.phoenix.cache;

import java.io.Closeable;
import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun.Function1;
import org.mapdb.TxBlock;
import org.mapdb.TxMaker;
import org.mapdb.TxRollbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.SignedBytes;
import com.heliosapm.phoenix.cache.CachedTSMeta.CachedTSMetaSerializer;
import com.heliosapm.phoenix.cache.CachedUIDMeta.CachedUIDMetaSerializer;

/**
 * <p>Title: CacheImpl</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.cache.CacheImpl</code></p>
 */

public class CacheImpl implements Closeable {
	private static final Logger log = LoggerFactory .getLogger(CacheImpl.class);
	private static final Map<String, CacheImpl> dbs = new ConcurrentHashMap<String, CacheImpl>();
	
	/** The name of the CacheTSMeta map */
	public static final String TSMETA_NAME = "tsmeta";
	/** The name of the TagK map */
	public static final String TAGK_NAME = "tagk";
	/** The name of the TagV map */
	public static final String TAGV_NAME = "tagv";
	/** The name of the Metric map */
	public static final String METRIC_NAME = "metric";
	
	final File dbFile;
	final DBMaker.Maker dbMaker;
	final TxMaker txMaker;
	final boolean arch64bit;

	public static CacheImpl getInstance(final String fileName) {
		if(fileName==null || fileName.trim().isEmpty()) throw new IllegalArgumentException("The passed file name was null or empty");
		final File f = new File(fileName.trim());
		final String key = f.getAbsolutePath();
		CacheImpl ci = dbs.get(key);
		if(ci==null) {
			synchronized(dbs) {
				ci = dbs.get(key);
				if(ci==null) {
					ci = new CacheImpl(f);
					dbs.put(key, ci);
				}
			}
		}
		return ci;
	}
	
	final static Comparator<String> STRING_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(final String o1, final String o2) {
			return o1.compareTo(o2);
		}		
	};
	final static Comparator<byte[]> BYTEARR_COMPARATOR = SignedBytes.lexicographicalComparator();
	
	
	/**
	 * Creates a new CacheImpl
	 * @param dbFile The file where the cache will be persisted
	 */
	private CacheImpl(final File dbFile) {
		this.dbFile = dbFile;
		final String arch = System.getProperty("os.arch","noarch");
		final String os = System.getProperty("os.name","").toLowerCase();
		final boolean isWin = os.contains("windows");
		log.info("IsWindows: {}", isWin);
		arch64bit = arch.contains("64");
		DBMaker.Maker dbMaker = DBMaker.fileDB(this.dbFile);
		if(!isWin) dbMaker = dbMaker.fileMmapEnableIfSupported();
		dbMaker = dbMaker.cacheSize(20000);
		
		txMaker = dbMaker.makeTxMaker();
		final DB db = txMaker.makeTx();		
//		final DB db = dbMaker.make();
		db.treeMapCreate(TSMETA_NAME)
			.comparator(STRING_COMPARATOR)			
			.keySerializer(BTreeKeySerializer.STRING)
			.valueSerializer(CachedTSMetaSerializer.INSTANCE)			
			.makeOrGet();	
		db.treeMapCreate(TAGK_NAME)
			.comparator(STRING_COMPARATOR)			
			.keySerializer(BTreeKeySerializer.STRING)
			.valueSerializer(CachedUIDMetaSerializer.INSTANCE)			
			.makeOrGet();	
		db.treeMapCreate(TAGV_NAME)
			.comparator(STRING_COMPARATOR)			
			.keySerializer(BTreeKeySerializer.STRING)
			.valueSerializer(CachedUIDMetaSerializer.INSTANCE)			
			.makeOrGet();	
		db.treeMapCreate(METRIC_NAME)
			.comparator(STRING_COMPARATOR)			
			.keySerializer(BTreeKeySerializer.STRING)
			.valueSerializer(CachedUIDMetaSerializer.INSTANCE)			
			.makeOrGet();	
		
		
		
		db.commit();
		db.close();
		txMaker.close();
		this.dbMaker = dbMaker;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("CacheImpl Test");

	}
	
	@SuppressWarnings("unchecked")
	public <K, T, M extends ConcurrentNavigableMap<K, T> & Closeable> M  getTSMetaCache() {
		final DB db =  dbMaker.makeTxMaker().makeTx();
		return (M) db.treeMap(TSMETA_NAME);
	}

	/**
	 * Returns a transactionally isolated view of the underlying db
	 * @return a transactionally isolated view of the underlying db
	 * @see org.mapdb.TxMaker#makeTx()
	 */
	public DB makeTx() {
		return dbMaker.makeTxMaker().makeTx();		
	}
	
	public TxMaker makeTxMaker() {
		return dbMaker.makeTxMaker();
	}

	/**
	 * Closes all resources for this DB
	 */
	@Override
	public void close() {
		try { txMaker.close(); } catch (Exception x) {/* No Op */}
	}

	/**
	 * Wraps single transaction in a block
	 * @param txBlock the block
	 * @see org.mapdb.TxMaker#execute(org.mapdb.TxBlock)
	 */
	public void execute(TxBlock txBlock) {
		final TxMaker tx = dbMaker.makeTxMaker();
		tx.execute(txBlock);
		tx.close();
		//.execute(txBlock);		
	}
	
	public void clearTSMetas() {
		execute(new TxBlock(){
			/**
			 * {@inheritDoc}
			 * @see org.mapdb.TxBlock#tx(org.mapdb.DB)
			 */
			@Override
			public void tx(final DB db) throws TxRollbackException {
				db.treeMap(TSMETA_NAME).clear();
				
			}
		});
	}

	/**
	 * Executes the passed function in a DB transaction
	 * @param txBlock The function to execute
	 * @return The return value of the function
	 * @see org.mapdb.TxMaker#execute(org.mapdb.Fun.Function1)
	 */
	public <A> A execute(final Function1<A, DB> txBlock) {
		return txMaker.execute(txBlock);
	}
	
	
	
	
}
