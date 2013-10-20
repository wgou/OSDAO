package org.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * @author 苟伟
 *
 */
public class Tools {
	//过滤特殊字符
	private static final String regEx="[`_~\\-!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	private static final String get = "get";
	private static final String set = "set";
	private static final String is = "is";
	//表名转类名
	public static String tableNameConvertClassName(String tableName){
		String className = tableName.toLowerCase();
		className = className.substring(0, 1).toUpperCase() + className.substring(1);
		className = _convertUpperCase(className);
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(className);
		return m.replaceAll("").trim();
	}
	//列名转类属性名
	public static String columnNameConvertPropertyName(String cloumnName){
		String propertyName = cloumnName.toLowerCase();
		propertyName = _convertUpperCase(propertyName);
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(propertyName);
		return m.replaceAll("").trim();
	}
	//封装get方法名
	public static String getMethodName(String propertyName, Class<?> propertyType){
		if( boolean.class == propertyType || Boolean.class == propertyType){
			return is + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		}else{
			return get + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		}
	}
	//封装set方法名
	public static String setMethodName(String propertyName){
		return set + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}
	//封装属性类型
	public static String propertyType(String propertyType){
		return propertyType.substring(propertyType.lastIndexOf(".")+1);
	}
	
	//去下划线，将后一个字母转化为大写
	private static String _convertUpperCase(String name){
		String converName = "";
		String[] _index  = name.split("_");
		if(_index.length > 0){
			for(int i = 0;i<_index.length;i++){
				if(i!=0){
					converName +=_index[i].substring(0,1).toUpperCase() + _index[i].substring(1);
				}else{
					converName += _index[i];
				}
			}
		}else{
			converName = name;
		}
		return converName;
	}
	//返回类型检查,将写入javaBean import
	public static String checkImport(Class<?> clazz){
		String importList = null;
		if(clazz== java.sql.Date.class){
			importList = "java.sql.Date";
		}else if(clazz == java.sql.Timestamp.class){
			importList = "java.sql.Timestamp";
		}else if(clazz == java.sql.Time.class){
			importList = "java.sql.Time";
		}else if(clazz == java.sql.Clob.class){
			importList = "java.sql.Clob";
		}else if(clazz== java.math.BigDecimal.class){
			importList = "java.math.BigDecimal";
		}
		return importList;
	}
	
	
	public static void main(String[] args) {
		System.out.println(tableNameConvertClassName("_tbfiles~tablename"));
		System.out.println(columnNameConvertPropertyName("tbfilesTablename"));
	}
}
