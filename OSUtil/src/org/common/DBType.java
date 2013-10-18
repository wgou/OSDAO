package org.common;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DBType {
	private static final Map<String,Class<?>> oracleMap = new HashMap<String,Class<?>>(); 
	static{
		oracleMap.put(OracleType.BINARY_DOUBLE.toString(),double.class);
		oracleMap.put(OracleType.BINARY_FLOAT.toString(),float.class);
		oracleMap.put(OracleType.BLOB.toString(),String.class);
		oracleMap.put(OracleType.CHAR.toString(),char.class);
		oracleMap.put(OracleType.CLOB.toString(),String.class);
		oracleMap.put(OracleType.DATE.toString(),Date.class);
		oracleMap.put(OracleType.LONG.toString(),long.class);
		oracleMap.put(OracleType.NUMBER.toString(),int.class);
		oracleMap.put(OracleType.NVARCHAR2.toString(),String.class);
		oracleMap.put(OracleType.TIMESTAMP.toString(),Timestamp.class);
		oracleMap.put(OracleType.VARCHAR2.toString(),String.class);
		
	}
	
	public static Class<?>  converClass(String columnType){
		//TIMESTAMP(6) 等等单独处理
		if(columnType.startsWith("TIMESTAMP")){
			return  oracleMap.get("TIMESTAMP");
		}
		Class<?> clazz = oracleMap.get(columnType);
		if( clazz== null){  //如 LONG RWA
			return oracleMap.get("NVARCHAR2");
		}
		return clazz;
	}
	
	
}
