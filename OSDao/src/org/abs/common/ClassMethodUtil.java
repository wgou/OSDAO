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
 * Class and method ���� Table ����������
 * ��Annocation����,��װ���ݿ������Ҫ����
 * �� Ŀ �� ��: AbsDao
 * ��    ��   ��: ClassMethodUtil.java
 * ��	��: 
 * ��	��: 
 * ��	�ߣ�  ��ΰ
 * �� �� ʱ ��: 2013-10-6 23:02
 * ��	��: V1.0 
 * *************************************/
public class ClassMethodUtil {
	private final static Map<String, Map<String, Map<String, Method>>> classMethodMap = new HashMap<String, Map<String, Map<String, Method>>>();
	private static final String getPrefix = "^(?:get|is)"; 
	private static final String setPrefix = "set"; 
	//��bean�е�get method�����ϵ�colunmע���װΪMap������bean��Ӧ���ݿ������Ͷ�Ӧֵ
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
	//�������bean ��set����
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
	//�õ�get/set����
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
	//�õ�ʵ��bean��Ӧ�ı���
	public static String getTableName(Object obj){
		Class<?> clazz = (obj instanceof Class<?>)?(Class<?>)obj:obj.getClass();
		boolean isTableName = clazz.isAnnotationPresent(Table.class);
		if(!isTableName) throw new AbsException(clazz + " not extis table annotation ");
		Table table = clazz.getAnnotation(Table.class);
		String tableName = table.name();
		if(StringUtils.isEmpty(tableName)) throw new AbsException(clazz + " table annotation name is null");
		return tableName;
	}
	//�жϸ÷����Ƿ�Ϊis/get����
	public static boolean isGetMethod(Method method){
		return (method.getName().matches(getPrefix+".*") && !method.getName().matches(getPrefix+"$"));
	}
	//�жϸ�is/get�����Ƿ���в���
	public static boolean isGetMethodParams(Method method){
		return method.getGenericParameterTypes().length == 0;
	}
	//�жϸ÷����Ƿ�Ϊset����
	public static boolean isSetMethod(Method method){
		return (method.getName().startsWith(setPrefix) && !setPrefix.equals(method.getName()));
	}
	//�ж�set�����Ƿ�ֻ����һ������
	public static boolean isSetMethodParams(Method method){
		return method.getGenericParameterTypes().length == 1;
	}
	//�жϷ������������Ƿ�Ϊ������������
	public static boolean methodReturnType(Method method){
		return dbClazz.contains(method.getReturnType());
	}
	//�жϷ�����������
	public static boolean methodParamType(Method method){
		return dbClazz.contains(method.getParameterTypes()[0]);
	}
	//��ȡ�������ϵ�columnע��ֵ
	public static String getColumnName(Method method){
		boolean isColumn = method.isAnnotationPresent(Column.class);
		if(!isColumn) throw new AbsException("method " + method.getName() +" not exist column annotation");
		Column cloumn = method.getAnnotation(Column.class);
		return cloumn.name();
	}
	//���get������ֵ
	public static Object getMethodValue(Object obj,Method method) throws IllegalArgumentException, IllegalAccessException, Exception{
		Object o = method.invoke(obj);
		return o;
	}
	//�жϸ÷������Ƿ��������ע��
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
