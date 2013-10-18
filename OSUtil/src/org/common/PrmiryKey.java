package org.common;

public class PrmiryKey {
	private String primaryKeyName;
	private Class<?> primaryKeyType;
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}
	public Class<?> getPrimaryKeyType() {
		return primaryKeyType;
	}
	public void setPrimaryKeyType(Class<?> primaryKeyType) {
		this.primaryKeyType = primaryKeyType;
	}
}
