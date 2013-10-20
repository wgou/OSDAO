package org.abs.dao;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.abs.common.AbsException;
import org.abs.common.ClassMethodUtil;
import org.abs.common.SQL;
import org.apache.commons.lang.StringUtils;

/*************************************
 * 反射构建SQL
 * 项 目 名 称: AbsDao
 * 文    件   名: AbsDao.java
 * 标	题: 
 * 描	述: 
 * 作	者：  苟伟
 * 创 建 时 间: 2013-10-7 15:34
 * 版	本: V1.0 
 * *************************************/
public abstract class AbsDao {
	//构建insert sql语句
	protected final  SQL buildInsetSql(Object o) throws RuntimeException, IllegalAccessException, Exception{
		SQL sql = ClassMethodUtil.getSQL(o);
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("insert into " + sql.getTableName()+" " );
		StringBuilder columnName = new StringBuilder();
		columnName.append("(");
		StringBuilder valueName = new StringBuilder();
		valueName.append("(");
		for(String cName : sql.getCloumnName()){
			if(!columnName.toString().equals("(")){columnName.append(",");}
				columnName.append(cName);
			if(!valueName.toString().equals("(")){valueName.append(",");}
				valueName.append("?");
		}
		columnName.append(")");
		valueName.append(")");
		sbSQL.append(columnName+" values " + valueName);
		sql.setSql(sbSQL.toString());
		return sql;
	}

	//构建根据主键id delete table 数据的sql
	protected final  String buildDelteSQL(Class<?> clazz) throws Exception{
		Method[] methods = clazz.getMethods();
		String pkName = null;
		for(Method method : methods){
			if(ClassMethodUtil.isGetMethod(method) && ClassMethodUtil.isGetMethodParams(method) && ClassMethodUtil.methodReturnType(method) && ClassMethodUtil.isPrimaryKey(method))
				pkName = ClassMethodUtil.getColumnName(method);
		}
		if(StringUtils.isEmpty(pkName)) throw new AbsException("dont't delete not extis primarykey is bean");
		String tableName = ClassMethodUtil.getTableName(clazz);
		StringBuilder deleteSQL = new StringBuilder();
		deleteSQL.append("delete from "+tableName + " t where t."+pkName+"=?");
		return deleteSQL.toString();
	}
	//构建update sql语句
	protected final  SQL buildUpdateSQL(Object obj) throws IllegalArgumentException, IllegalAccessException, Exception{
		SQL sql = ClassMethodUtil.getSQL(obj);
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("update " + sql.getTableName() + " set " );
		List<String> cloumnNames = sql.getCloumnName();
		String pkName = sql.getPrimaryKey();
		int i = 0;
		for(String cloumn : cloumnNames){
			if(!cloumn.equals(pkName)){
				if(i != 0){sbSQL.append(",");}
				sbSQL.append(cloumn+" = ? ");
				i++;
			}
		}
		sql.setSql(sbSQL.append(" where "+pkName+"=?").toString());
		return sql;
	}
	//构建根据主键id select table 数据的sql
	protected final  String buildQueryByIdSQL(Class<?> clazz,String... columnNames) throws Exception{
		StringBuilder querySQL = new StringBuilder();
		String tableName = ClassMethodUtil.getTableName(clazz);
		if(columnNames != null && columnNames.length > 0 &&  columnNames[0] != null){
			querySQL.append("select ");
			for(int i=0;i<columnNames.length;i++){
				if(i != 0){
					querySQL.append(",");
				}
				querySQL.append(columnNames);
			}
		}else{
			querySQL.append("select * ");
		}
		Method[] methods = clazz.getMethods();
		String pkName = null;
		for(Method method : methods){
			if(ClassMethodUtil.isGetMethod(method) && ClassMethodUtil.isGetMethodParams(method) && ClassMethodUtil.methodReturnType(method) && ClassMethodUtil.isPrimaryKey(method))
				pkName = ClassMethodUtil.getColumnName(method);
		}
		if(StringUtils.isEmpty(pkName)) throw new AbsException("dont't search not extis primarykey is bean");
		querySQL.append(" from "+tableName + " t where t."+pkName+"=?");
		return querySQL.toString();
	}
	//构建select count(*) from table sql
	protected final  String getCountSQL(String sql){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ( ").append(sql).append(" ) t");
		return sb.toString();
	}
	//构建分页sql
	protected  final String getPagedListSQL(String sql,int start,int end) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from (select a.*,rownum rn from  ").append("(")
		.append(sql).append(")a").append(" where rownum <=").append(end)
		.append(")").append("where rn >").append(start);
		System.out.println(sb.toString());
		return sb.toString();
		
	}
	//将resultSet封装为bean对象
	protected  <T>T getResultSetToBean(Class<T> clazz,ResultSet rs) throws Exception{
		T t = null;
		try {
			 t = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(clazz.getName()+ "cant't be instance!" + e);
		} 
		Map<String,Method> getMethodMap = ClassMethodUtil.getGetMethodMap(clazz);
		Map<String,Method> setMethodMap = ClassMethodUtil.getSetMethodMap(clazz);
		for(Map.Entry<String,Method> getMethod : getMethodMap.entrySet()){
			for(Map.Entry<String,Method> setMethod : setMethodMap.entrySet()){
				if(getMethod.getKey().equals(setMethod.getKey())){
					ClassMethodUtil.getInvokeSetBean(t, rs, getMethod.getValue(),setMethod.getValue());
				}
			}
		}
		
		return  t;
	}
	//将resultSet封装为List<map<String,Object>>对象，Map<k,V> k=columnName,V=value
	protected final  List<Map<String,Object>> getResultListMap(ResultSet rs) throws SQLException{
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String,Object> dataMap = null;
		while (rs.next()) {
			dataMap = new HashMap<String, Object>();
			ResultSetMetaData rsm = rs.getMetaData();
			for(int i = 1;i< rsm.getColumnCount();i++){
				dataMap.put(rsm.getColumnName(i), rs.getString(i));
			}
			dataList.add(dataMap);
		}
		return dataList;
	}
	
	/**
	 * 批量操作，返回参数和主键值
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public final Map<Serializable[],Object[][]> getBathParam(List<Object> objs) throws IllegalArgumentException, IllegalAccessException, Exception{
		Map<Serializable[],Object[][]> map = new HashMap<Serializable[], Object[][]>(1);
		Serializable[] serializable = new Serializable[objs.size()];
		Map<String,Method> getMethodMap = ClassMethodUtil.getGetMethodMap(objs.get(0).getClass());
		Object[][] paras = new Object[objs.size()][getMethodMap.size()];
		int k = 0,j = 0,i=0;
		for(Object obj : objs){
			for(Map.Entry<String,Method> m : getMethodMap.entrySet()){
				if(ClassMethodUtil.isPrimaryKey(m.getValue()))
				{
					 Object PKValue = ClassMethodUtil.getMethodValue(obj,m.getValue());
					 if(PKValue == null ||  "".equals(PKValue) || Integer.parseInt(PKValue.toString()) == 0){
						 PKValue = BaseDaoAssist.getprimaryKeyValue(m.getValue(),ClassMethodUtil.getTableName(objs.get(0)));
					 }
					 serializable[i++] = (Serializable)PKValue;
					 paras[k][j++] = PKValue;
				}else{
					paras[k][j++] = ClassMethodUtil.getMethodValue(obj, m.getValue());
				}
			}
			k++;
			j=0;
		}
		map.put(serializable, paras);
		return map;
	}
		
}
