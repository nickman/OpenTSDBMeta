/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.phoenix.udf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.function.ScalarFunction;
import org.apache.phoenix.schema.tuple.Tuple;
import org.apache.phoenix.schema.types.PDataType;
import org.apache.phoenix.schema.types.PInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: BinaryToInteger</p>
 * <p>Description: Phoenix UDF to convert OpenTSDB binary values in HBase tables to integers</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.udf.BinaryToInteger</code></p>
 */

public class BinaryToInteger extends ScalarFunction {
	/** The SQL name of this function */
	public static final String NAME = "BINTOINT";
	
    private static final Logger log = LoggerFactory .getLogger(BinaryToInteger.class);

	/**
	 * Creates a new BinaryToNumber
	 */
	public BinaryToInteger() {	
		log.warn("Created BinaryToInteger instance");
	}

	/**
	 * Creates a new BinaryToNumber
	 * @param children not sure
	 */
	public BinaryToInteger(final List<Expression> children) {
		super(children);
		log.warn("Created BinaryToInteger instance");
	}
	
	static final Charset UTF8 = Charset.forName("UTF8");
	
	public static byte[] getBytes(final ImmutableBytesWritable ptr) {
		final int len = ptr.getLength();
		final int offset = ptr.getOffset();
		final byte[] bytes = ptr.get();
		final byte[] b = new byte[len];
		System.arraycopy(bytes, offset, b, 0, len);
		return b;
	}
	
	public static String printTuple(final Tuple tuple) {
		final StringBuilder b = new StringBuilder("Tuple:");
		b.append("\n\tSize:").append(tuple.size());
		ImmutableBytesWritable ptr = new ImmutableBytesWritable();
		tuple.getKey(ptr);
		
		b.append("\n\tKey:").append(new String(getBytes(ptr), UTF8));
		for(int i = 0; i < tuple.size(); i++) {
			
			b.append("\n\t\tCell#").append(i).append(":");			
			final Cell cell = tuple.getValue(i);
			
			b.append("\n\t\t(").append(cell.getClass().getName()).append(")");
			b.append("\n\t\tTimestamp:[").append(new Date(cell.getTimestamp())).append("]");
			b.append("\n\t\tFamily:").append(new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength(), UTF8));
			b.append("\n\t\tQualfier:").append(new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength(), UTF8));
			b.append("\n\t\tRow:[").append(new String(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength(), UTF8)).append("]");
			b.append("\n\t\tValue:[").append(Arrays.toString(cell.getValueArray())).append("]");
			b.append("\n\t\tTags:[").append(new String(cell.getTagsArray(), cell.getTagsOffset(), cell.getTagsLength(), UTF8)).append("]");
			
		}
		return b.toString();
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.phoenix.expression.Expression#evaluate(org.apache.phoenix.schema.tuple.Tuple, org.apache.hadoop.hbase.io.ImmutableBytesWritable)
	 */
	@Override
	public boolean evaluate(final Tuple tuple, final ImmutableBytesWritable ptr) {
		log.warn("Tuple: {}", printTuple(tuple));
		log.warn("Tuple: {}", tuple);
		try {
	        Expression arg = getChildren().get(0);
	        log.warn("Arg: ({}): [{}]", arg.getClass().getName(), arg);
	        if (!arg.evaluate(tuple, ptr)) {
	            return false;
	        }
	
	        int targetOffset = ptr.getLength();
	        if (targetOffset == 0) {
	            return true;
	        }
	        final int x = ByteBuffer
	        		.wrap(ptr.get())
	        		.asIntBuffer()
	        		.get(0);
	        log.warn("Int generated: [{}], Ptr Length: [{}]", x, targetOffset);
	        byte[] byteValue = getDataType().toBytes(x);
	        ptr.set(byteValue);
	        log.warn("Done");
	        return true;
	        
//	        byte[] source = ptr.get();
//	        final int len = Math.min(source.length, 4);
//	        byte[] target = new byte[4];
//	        System.arraycopy(source, 0, target, 0, len);
//	        final int x = ByteBuffer.wrap(target).asIntBuffer().get(0);
//	        byte[] byteValue = getDataType().toBytes(x);
//	        ptr.set(byteValue);
//	        return true;
		} catch (Throwable t) {
			log.error("Failed to execute bintoint", t);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.phoenix.schema.PDatum#getDataType()
	 */
	@Override
	public PDataType getDataType() {
		return PInteger.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 * @see org.apache.phoenix.expression.function.FunctionExpression#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

}
