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
package com.heliosapm.phoenix.udf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.function.ScalarFunction;
import org.apache.phoenix.schema.tuple.Tuple;
import org.apache.phoenix.schema.types.PDataType;
import org.apache.phoenix.schema.types.PInteger;
import org.apache.phoenix.schema.types.PVarchar;

/**
 * <p>Title: OpenTSDBFunctions</p>
 * <p>Description: A set of OpenTSDB specific Phoenix UDFs</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.udf.OpenTSDBFunctions</code></p>
 */

public class OpenTSDBFunctions {
	
	/** UTF8 Character Set */
	public static final Charset UTF8 = Charset.forName("UTF8");
  /** Hex decode characters */
  private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

  /**
   * Returns the passed byte array in Hex string format
   * @param data The bytes to format
   * @return the hex string
   */
  public static String printHexBinary(byte[] data) {
  	if(data==null || data.length==0) return "";
      StringBuilder r = new StringBuilder(data.length*2);
      for ( byte b : data) {
          r.append(hexCode[(b >> 4) & 0xF]);
          r.append(hexCode[(b & 0xF)]);
      }
      return r.toString();
  }
	
	
	/**
	 * <p>Title: AbstractScalarFunction</p>
	 * <p>Description: A base abstract UDF</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>com.heliosapm.phoenix.udf.OpenTSDBFunctions.AbstractScalarFunction</code></p>
	 * @param <T> The assumed data tyoe
	 */
	public static abstract class AbstractScalarFunction<T> extends ScalarFunction {
		/** The UDF data type */
		PDataType<T> dataType;
		/** The UDF name */
		final String name;
		
		static final List<Expression> EMPTY_EXPR_LIST = Collections.unmodifiableList(new ArrayList<Expression>(0));
		
		/**
		 * Creates a new AbstractScalarFunction
		 * @param dataType The phoenix data type
		 * @param name The UDF name
		 * @param children The UDF children
		 */
		private AbstractScalarFunction(final PDataType<T> dataType, final String name, final List<Expression> children) {
			super(children==null ? EMPTY_EXPR_LIST : children);
			this.dataType = dataType;
			this.name = name;
		}

		
		/**
		 * Creates a new AbstractScalarFunction
		 * @param dataType The phoenix data type
		 * @param name The UDF name
		 */
		public AbstractScalarFunction(final PDataType<T> dataType, final String name) {
			this(dataType, name, EMPTY_EXPR_LIST);
		}
		
		

		/**
		 * {@inheritDoc}
		 * @see org.apache.phoenix.expression.Expression#evaluate(org.apache.phoenix.schema.tuple.Tuple, org.apache.hadoop.hbase.io.ImmutableBytesWritable)
		 */
		@Override
		public boolean evaluate(final Tuple tuple, final ImmutableBytesWritable ptr) {			
			
			
			return true;
		}
		
//		protected abstract boolean eval(final Tuple tuple, final ImmutableBytesWritable ptr);

		/**
		 * {@inheritDoc}
		 * @see org.apache.phoenix.schema.PDatum#getDataType()
		 */
		@Override
		public PDataType<T> getDataType() {
			return dataType;
		}

		/**
		 * {@inheritDoc}
		 * @see org.apache.phoenix.expression.function.FunctionExpression#getName()
		 */
		@Override
		public String getName() {
			return name;
		}
		
	}
	
	/**
	 * <p>Title: ToInt</p>
	 * <p>Description: Converts supported data types to an Integer</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>com.heliosapm.phoenix.udf.OpenTSDBFunctions.ToInt</code></p>
	 */
	public static class ToInt extends AbstractScalarFunction<Integer> {

		/**
		 * Creates a new ToInt
		 */
		public ToInt() {
			super(PInteger.INSTANCE, "TOINT");
		}
		
		/**
		 * Creates a new ToInt
		 * @param children The UDF's children
		 */
		public ToInt(final List<Expression> children) {
			super(PInteger.INSTANCE, "TOINT", children);
		}
		
		
		@Override
		public boolean evaluate(final Tuple tuple, final ImmutableBytesWritable ptr) {			
      final Expression arg = getChildren().get(0);
      if (!arg.evaluate(tuple, ptr)) {
          return false;
      }
      final int targetOffset = ptr.getLength();
      if (targetOffset == 0) {
          return true;
      }
      ptr.set(getDataType().toBytes(ByteBuffer.wrap(ptr.get()).asIntBuffer().get(0)));
      return true;
		}		
		
	}
	
	
	public static class ToHex extends AbstractScalarFunction<String> {

		/**
		 * Creates a new ToHex
		 */
		public ToHex() {
			super(PVarchar.INSTANCE, "TOHEX");
		}
		
		/**
		 * Creates a new ToInt
		 * @param children The UDF's children
		 */
		public ToHex(final List<Expression> children) {
			super(PVarchar.INSTANCE, "TOHEX", children);
		}
		
		
		public List<Object> parseArguments(final Tuple tuple, final ImmutableBytesWritable ptr) {
			final List<Expression> children = getChildren();
			final int size = children.size();
			if(size==0) return Collections.emptyList();
			final List<Object> results = new ArrayList<Object>(size);
			for(Expression expr: children) {
				if(!expr.evaluate(tuple, ptr)) {
					break;
				} 
				PDataType<?> pd = expr.getDataType();
				XX  Need to get Offset HERE !!!
				results.add(pd.toObject(ptr.get()));
				expr.reset();
			}
			return results;
		}
		
		private static final byte[] EMPTY_STR = "".getBytes(UTF8); 
		
		/**
		 * {@inheritDoc}
		 * @see com.heliosapm.phoenix.udf.OpenTSDBFunctions.AbstractScalarFunction#evaluate(org.apache.phoenix.schema.tuple.Tuple, org.apache.hadoop.hbase.io.ImmutableBytesWritable)
		 */
		@Override
		public boolean evaluate(final Tuple tuple, final ImmutableBytesWritable ptr) {
			final List<Object> args = parseArguments(tuple, ptr);
   // =============================================
      final byte[] input = (byte[])args.get(0);
			final Integer argOffset = (Integer)args.get(1);
      final Integer argLength = (Integer)args.get(2);
      // =============================================
      if(input==null || input.length==0) {
      	ptr.set(EMPTY_STR);
      } else {
      	if(argOffset!=null) {
      		if(argLength!=null) {
      			final byte[] b = new byte[argLength];
      			System.arraycopy(input, argOffset, b, 0, argLength);
      			ptr.set(printHexBinary(b).getBytes(UTF8));
      		} else {
      			final int actualLen = input.length - argOffset;
      			final byte[] b = new byte[actualLen];
      			System.arraycopy(input, argOffset, b, 0, actualLen);      			
      			ptr.set(printHexBinary(b).getBytes(UTF8));
      		}
      	} else {
      		ptr.set(printHexBinary(input).getBytes(UTF8));
      	}
      }
      return true; 
		}				
	}
	
	
}
