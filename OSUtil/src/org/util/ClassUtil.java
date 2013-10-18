package org.util;

import java.util.HashMap;
import java.util.Map;

import org.common.DBType;

/**
 * ���ݿ�������ת��Ϊjava��������
 * @author ��ΰ
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
		//��ʵ��oracle���ݿ�����ת��
		return DBType.converClass(columnType);
		
	}
	
}
