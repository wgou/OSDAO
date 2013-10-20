package org.abs.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abs.annotation.Column;
import org.abs.annotation.KeyType;
import org.abs.annotation.Table;
import org.abs.dao.BaseDaoAssist;
import org.apache.commons.lang.StringUtils;
/*************************************
 * Class and method 关联 Table 操作工具类
 * 对Annocation解析,封装数据库操作需要属性
 * 项 目 名 称: AbsDao
 * 文    件   名: ClassMethodUtil.java
 * 标	题: 
 * 描	述: 
 * 作	者：  苟伟
 * 创 建 时 间: 2013-10-6 23:02
 * 版	本: V1.0 
 * *************************************/
public class ClassMethodUtil {
	private final static Map<String, Map<String, Map<String, Method>>> classMethodMap = new HashMap<String, Map<String, Map<String, Method>>>();
	private static final String getPrefix = "^(?:get|is)"; 
	private static final String setPrefix = "set"; 
	//将bean中的get method方法上的colunm注解封装为Map，返回bean对应数据库列名和对应值
	public static SQL getSQL(Object obj) throws IllegalArgumentException, IllegalAccessException, Exception{
		SQL sql = new SQL();
		List<String> columnName = new ArrayList<String>();
		List<Object> paramValue = new ArrayList<Object>();
		Map<String,Method> getMethodMap= getGetMethodMap(obj.getClass());
		for(Map.Entry<String, Method> m : getMethodMap.entrySet()){
			columnName.add(getColumnName(m.getValue()));
			if(isPrimaryKey(m.getValue()))
			{
				 Object PKValue = getMethodValue(obj,m.getValue());
				 if(PKValue == null ||  "".equals(PKValue) || Integer.parseInt(PKValue.toString()) == 0){
					 PKValue = BaseDaoAssist.getprimaryKeyValue(m.getValue(), getTableName(obj));
				 }
				paramValue.add(PKValue);
				sql.setPrimaryKey(getColumnName(m.getValue()));
				sql.setPrimaryKeyValue((Serializable)PKValue);
			}else{
				paramValue.add(getMethodValue(obj,m.getValue()));
			}
		}
		sql.setCloumnName(columnName);
		sql.setParamsValue(paramValue);
		sql.setTableName(getTableName(obj));
		return sql;
	}
	//反射调用bean 的set方法
	public static <T>void getInvokeSetBean(T clazz, ResultSet rs,
			Method getMethod,Method setMethod) throws IllegalAccessException,
			InvocationTargetException, SQLException {
		Object parameter = null;
		Class<?> clazzParamType = setMethod.getParameterTypes()[0];
		if(clazzParamType == String.class){
			parameter = rs.getString(getColumnName(getMethod));
		}else if(clazzParamType== Integer.class || clazzParamType== int.class){
			parameter =rs.getInt(getColumnName(getMethod));
		}else if(clazzParamType== Double.class || clazzParamType== double.class){
			parameter =rs.getDouble(getColumnName(getMethod));
		}else if(clazzParamType== Long.class || clazzParamType== long.class){
			parameter =rs.getLong(getColumnName(getMethod));
		}else if(clazzParamType== Float.class || clazzParamType== float.class){
			parameter =rs.getFloat(getColumnName(getMethod));
		}else if(clazzParamType== Short.class || clazzParamType== short.class){
			parameter =rs.getShort(getColumnName(getMethod));
		}else if(clazzParamType== Boolean.class || clazzParamType== boolean.class){
			parameter =rs.getBoolean(getColumnName(getMethod));
		}else if(clazzParamType== java.sql.Date.class){
			parameter =rs.getDate(getColumnName(getMethod));
		}else if(clazzParamType== Timestamp.class){
			parameter =rs.getTimestamp(getColumnName(getMethod));
		}else if(clazzParamType== Time.class){
			parameter =rs.getTime(getColumnName(getMethod));
		}else if(clazzParamType== Clob.class){
			parameter =rs.getClob(getColumnName(getMethod));
		}else if(clazzParamType== BigDecimal.class){
			parameter =rs.getBigDecimal(getColumnName(getMethod));
		}
		setMethod.invoke(clazz, parameter);
	}
	//得到get/set方法
	public static Map<String, Map<String, Method>> getMethodMapByClass(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		int initialCapacity = methods.length / 2;
		Map<String, Method> setMethodMap = new HashMap<String, Method>(initialCapacity);
		Map<String, Method> getMethodMap = new HashMap<String, Method>(initialCapacity + 1);
		for (Method method : methods) {
			String name = method.getName();
			if (isSetMethod(method) && isSetMethodParams(method) && methodParamType(method)) {
				setMethodMap.put(toStandardFiledName(name), method);
			} else if (isGetMethod(method) && isGetMethodParams(method) && methodReturnType(method)) {
				getMethodMap.put(toStandardFiledName(name), method);
			}
		}
		Map<String, Map<String, Method>> methodmap = new HashMap<String, Map<String, Method>>(2);
		methodmap.put(setPrefix, setMethodMap);
		methodmap.put(getPrefix, getMethodMap);
		return methodmap;
	} /* return true when method is standard setter */

	public static String toStandardFiledName(String name) {
	    int subIndex = name.startsWith("is") ? 2 : 3;        
		String fieldname = name.substring(subIndex);
		return fieldname.substring(0, 1).toLowerCase() + fieldname.substring(1);
	} 
	/** * return class's setter & getter from static map. * * @param clazz * @return */
	private static Map<String, Map<String, Method>> getMethodMap(Class<?> clazz) {
		String classname = clazz.getName();
		if (!classMethodMap.containsKey(classname)) {
			classMethodMap.put(classname, getMethodMapByClass(clazz));
		}
		return classMethodMap.get(classname);
	} /* return class's setter & getter */

	/** * return class's setter. * * @param clazz * @return */
	public static Map<String, Method> getSetMethodMap(Class<?> clazz) {
		return getMethodMap(clazz).get(setPrefix);
	}

	/** * return class's getter. * * @param clazz * @return */
	public static Map<String, Method> getGetMethodMap(Class<?> clazz) {
		return getMethodMap(clazz).get(getPrefix);
	}
	//得到实体bean对应的表名
	public static String getTableName(Object obj){
		Class<?> clazz = (obj instanceof Class<?>)?(Class<?>)obj:obj.getClass();
		boolean isTableName = clazz.isAnnotationPresent(Table.class);
		if(!isTableName) throw new AbsException(clazz + " not extis table annotation ");
		Table table = clazz.getAnnotation(Table.class);
		String tableName = table.name();
		if(StringUtils.isEmpty(tableName)) throw new AbsException(clazz + " table annotation name is null");
		return tableName;
	}
	//判断该方法是否为is/get方法
	public static boolean isGetMethod(Method method){
		return (method.getName().matches(getPrefix+".*") && !method.getName().matches(getPrefix+"$"));
	}
	//判断该is/get方法是否带有参数
	public static boolean isGetMethodParams(Method method){
		return method.getGenericParameterTypes().length == 0;
	}
	//判断该方法是否为set方法
	public static boolean isSetMethod(Method method){
		return (method.getName().startsWith(setPrefix) && !setPrefix.equals(method.getName()));
	}
	//判断set方法是否只具有一个参数
	public static boolean isSetMethodParams(Method method){
		return method.getGenericParameterTypes().length == 1;
	}
	//判断方法返回类型是否为包含数据类型
	public static boolean methodReturnType(Method method){
		return dbClazz.contains(method.getReturnType());
	}
	//判断方法参数类型
	public static boolean methodParamType(Method method){
		return dbClazz.contains(method.getParameterTypes()[0]);
	}
	//获取方法名上的column注解值
	public static String getColumnName(Method method){
		boolean isColumn = method.isAnnotationPresent(Column.class);
		if(!isColumn) throw new AbsException("method " + method.getName() +" not exist column annotation");
		Column cloumn = method.getAnnotation(Column.class);
		return cloumn.name();
	}
	//获得get方法的值
	public static Object getMethodValue(Object obj,Method method) throws IllegalArgumentException, IllegalAccessException, Exception{
		Object o = method.invoke(obj);
		return o;
	}
	//判断该方法上是否具有主键注释
	public static boolean isPrimaryKey(Method method){
		Column cloumn = method.getAnnotation(Column.class);
		return (cloumn != null && cloumn.type().type() != KeyType.NULL && cloumn.type().name() != "");
	}
	
	 public static final List<Class<?>>dbClazz = new ArrayList<Class<?>>();
	    static {
	        dbClazz.add(String.class);
	        dbClazz.add(Integer.TYPE);
	        dbClazz.add(Long.TYPE);
	        dbClazz.add(Float.TYPE);
	        dbClazz.add(Double.TYPE);
	        dbClazz.add(Boolean.TYPE);
	        
	        dbClazz.add(Boolean.class);
	        dbClazz.add(Integer.class);
	        dbClazz.add(Long.class);
	        dbClazz.add(Float.class);
	        dbClazz.add(Double.class);
	        
	        dbClazz.add(Byte.TYPE);
	        dbClazz.add(byte.class);
	        
	        dbClazz.add(Short.TYPE);
	        dbClazz.add(short.class);
	        
	        dbClazz.add(Timestamp.class);
	        dbClazz.add(Date.class);
	        dbClazz.add(Time.class);
	        dbClazz.add(java.util.Date.class);
	    }
	
	
}
