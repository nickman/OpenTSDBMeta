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

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.phoenix.expression.Expression;
import org.apache.phoenix.expression.LiteralExpression;
import org.apache.phoenix.expression.function.InstrFunction;
import org.apache.phoenix.schema.SortOrder;
import org.apache.phoenix.schema.types.PDataType;
import org.junit.Test;

/**
 * <p>Title: BinaryToIntegerTest</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.phoenix.udf.BinaryToIntegerTest</code></p>
 */

public class BinaryToIntegerTest {
	
    public static void inputExpression(String value, PDataType dataType, String strToSearch,Integer expected, SortOrder order) throws SQLException{
        Expression inputArg = LiteralExpression.newConstant(value,dataType,order);
        
        Expression strToSearchExp = LiteralExpression.newConstant(strToSearch,dataType);
        List<Expression> expressions = Arrays.<Expression>asList(inputArg,strToSearchExp);
        Expression instrFunction = new InstrFunction(expressions);
        ImmutableBytesWritable ptr = new ImmutableBytesWritable();
        instrFunction.evaluate(null,ptr);
        Integer result = (Integer) instrFunction.getDataType().toObject(ptr);
        assertTrue(result.compareTo(expected) == 0);
        
    }
    	
    @Test
    public void testBasicConversion() {
    	
    }
    
    public static void main(String[] args) {
    	log("BinaryToIntegerTest");
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rset = null;
    	try {
    		Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
    		Properties p = new Properties();
    		p.setProperty("phoenix.functions.allowUserDefinedFunctions", "true");
    		p.setProperty("hbase.tmp.dir", "/tmp/hbase-nwhitehead/hbase");
    		p.setProperty("hbase.rootdir", "/home/nwhitehead/services/hbase/hbase-0.98.13-hadoop2");
    		p.setProperty("hbase.dynamic.jars.dir", "/home/nwhitehead/services/hbase/hbase-0.98.13-hadoop2/lib");
    		conn = DriverManager.getConnection("jdbc:phoenix:localhost", p);
//    		conn = DriverManager.getConnection("jdbc:phoenix:192.168.1.161:2181", p);
    		ps = conn.prepareStatement("select PK, TOHEX(\"tagv\", 0, 3) from \"tsdb-uid\" where \"tagv\" is not null and PK = ?");
//    		ps = conn.prepareStatement("select BINTOINT(PK) from \"tsdb\" LIMIT 3");
    		ps.setString(1, "/proc");
    		rset = ps.executeQuery();
    		ResultSetMetaData rsmd = rset.getMetaData();
    		int cnt = 1;
    		for(int i = 1; i < rsmd.getColumnCount() + 1; i++) {
    			log(String.format("Col #%s: Name: [%s], Type: [%s]", i, rsmd.getColumnName(i), rsmd.getColumnTypeName(i)));
    		}
    		while(rset.next()) {
//    		log("tagv: [" + rset.getObject(2) + "]");
    			log("PK: [" + rset.getObject(1) + "]");
    			log("tagv: [" + rset.getObject(2) + "]");
    		}    		
    		rset.close();
    		ps.close();
    		log("====================================================================");
    		ps = conn.prepareStatement("select BINTOINT(PK) from \"tsdb\" LIMIT 3");
//    		ps.setString(1, "/proc");
    		rset = ps.executeQuery();
    		rsmd = rset.getMetaData();
    		cnt = 1;
    		for(int i = 1; i < rsmd.getColumnCount() + 1; i++) {
    			log(String.format("Col #%s: Name: [%s], Type: [%s]", i, rsmd.getColumnName(i), rsmd.getColumnTypeName(i)));
    		}
    		rset.close();
    		ps.close();
    		log("====================================================================");
    		
    	} catch (Exception ex) {
    		ex.printStackTrace(System.err);
    	} finally {
    		if(rset!=null) try { rset.close(); } catch (Exception x) {/* No Op */}
    		if(ps!=null) try { ps.close(); } catch (Exception x) {/* No Op */}
    		if(conn!=null) try { conn.close(); } catch (Exception x) {/* No Op */}
    	}
    }
    
    public static void log(Object msg) {
    	System.out.println(msg);
    }

}
