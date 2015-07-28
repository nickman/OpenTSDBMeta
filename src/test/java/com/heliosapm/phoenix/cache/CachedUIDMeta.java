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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import net.opentsdb.uid.UniqueId;

import org.mapdb.Serializer;

/**
 * <p>Title: CachedUIDMeta</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.cache.CachedUIDMeta</code></p>
 */

public class CachedUIDMeta implements Serializable {
	/**  */
	private static final long serialVersionUID = -6450385859720509631L;
	/** The UID name */
	final String name;
	/** The UIDMeta uid bytes */
	final byte[] uid;
	/** The UIDMeta uid as a hex string */
	final transient String uidHex;
	/** The unique id type (tagk, tagv, metric) */
	final byte type;
	
	/** Decode for byte type to UniqueId.UniqueIdType */
	public static final Map<Byte, UniqueId.UniqueIdType> TYPE_DECODE;
	
	static {
		final UniqueId.UniqueIdType[] values = UniqueId.UniqueIdType.values();
		Map<Byte, UniqueId.UniqueIdType> map = new HashMap<Byte, UniqueId.UniqueIdType>(values.length);
		for(UniqueId.UniqueIdType ut: UniqueId.UniqueIdType.values()) {
			map.put((byte)ut.ordinal(), ut);
		}
		TYPE_DECODE = Collections.unmodifiableMap(map);
	}


	/**
	 * Creates a new CachedUIDMeta
	 * @param name The UID name
	 * @param uid The UID key bytes
	 * @param type The UID type
	 */
	public CachedUIDMeta(final String name, final byte[] uid, final UniqueId.UniqueIdType type) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("The passed name was null or empty");
		if(uid==null || uid.length==0) throw new IllegalArgumentException("The passed uid was null or zero length");		
		this.name = name.trim();
		this.uid = uid;
		this.uidHex = DatatypeConverter.printHexBinary(this.uid);
		this.type = (byte)type.ordinal();
	}
	
	private CachedUIDMeta(final DataInput in, final int available) throws IOException {
		uid = Serializer.BYTE_ARRAY.deserialize(in, available);
		uidHex = DatatypeConverter.printHexBinary(uid);
		name = Serializer.STRING.deserialize(in, available);
		type = Serializer.BYTE.deserialize(in, available);
	}

	
	/**
	 * <p>Title: CachedUIDMetaSerializer</p>
	 * <p>Description: The MapDB serializer for CachedUIDMeta instances</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>com.heliosapm.phoenix.cache.CachedUIDMeta.CachedUIDMetaSerializer</code></p>
	 */
	public static class CachedUIDMetaSerializer extends Serializer<CachedUIDMeta> implements Comparator<CachedUIDMeta>, Serializable {
		/**  */
		private static final long serialVersionUID = -223494413495965027L;
		/** A UTF8 character set */
		public static final Charset UTF8 = Charset.forName("UTF8");
		/** A reusable instance */
		public static final CachedUIDMetaSerializer INSTANCE = new CachedUIDMetaSerializer();

		
		@Override
		public void serialize(final DataOutput out, final CachedUIDMeta value) throws IOException {
			BYTE_ARRAY.serialize(out, value.uid);
			STRING.serialize(out, value.name);
			BYTE.serialize(out, value.type);
		}

		@Override
		public CachedUIDMeta deserialize(final DataInput in, final int available) throws IOException {
			return available==0 ? null : new CachedUIDMeta(in, available);
		}

		@Override
		public int compare(final CachedUIDMeta o1, final CachedUIDMeta o2) {
			return o1.uidHex.compareTo(o2.uidHex);
		}
		
	}
	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(TYPE_DECODE.get(type).name())
		.append(":").append(name)
		.append(" (").append(uidHex).append(")")
		.toString();
	}
	
	
	

	/**
	 * Returns the UID name
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * Returns the uid key bytes
	 * @return the uid key bytes
	 */
	public byte[] getUid() {
		return uid;
	}



	/**
	 * Returns the uid key as a hex string 
	 * @return the uidHex
	 */
	public String getUidHex() {
		return uidHex;
	}

	/**
	 * Returns the UID type
	 * @return the type
	 */
	public UniqueId.UniqueIdType getType() {
		return TYPE_DECODE.get(type);
	}


	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type;
		result = prime * result + Arrays.hashCode(uid);
		return result;
	}



	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CachedUIDMeta))
			return false;
		CachedUIDMeta other = (CachedUIDMeta) obj;
		if (type != other.type)
			return false;
		if (!Arrays.equals(uid, other.uid))
			return false;
		return true;
	}


}
