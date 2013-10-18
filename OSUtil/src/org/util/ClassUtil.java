package org.util;

import java.util.HashMap;
import java.util.Map;

import org.common.DBType;

/**
 * 数据库列类型转化为java对象类型
 * @author 苟伟
 *
 */
public class ClassUtil {
	public static Map<String,Class<?>> columnConverMap(Map<String,String> mapTable){
		Map<String,Class<?>> converMap = new HashMap<String, Class<?>>();
		for(Map.Entry<String, String> map : mapTable.entrySet()){
			converMap.put(map.getKey(), converClass(map.getValue()));
		}
		return converMap;
	}
	
	private static Class<?> converClass(String columnType){
		//先实现oracle数据库类型转化
		return DBType.converClass(columnType);
		
	}
	
}
