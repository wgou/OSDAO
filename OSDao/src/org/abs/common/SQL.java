package org.abs.common;

import java.io.Serializable;
import java.util.List;

public class SQL {
	private String sql;//ƴ��֮�������sql
	private List<String> cloumnName;  //��������
	private List<Object> paramsValue;//��������
	private String tableName;//����
	private Serializable primaryKeyValue;//������ֵ
	private String primaryKey;//����
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<String> getCloumnName() {
		return cloumnName;
	}
	public void setCloumnName(List<String> cloumnName) {
		this.cloumnName = cloumnName;
	}
	public List<Object> getParamsValue() {
		return paramsValue;
	}
	public void setParamsValue(List<Object> paramsValue) {
		this.paramsValue = paramsValue;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Serializable getPrimaryKeyValue() {
		return primaryKeyValue;
	}
	public void setPrimaryKeyValue(Serializable primaryKeyValue) {
		this.primaryKeyValue = primaryKeyValue;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
}
