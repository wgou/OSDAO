package org.common;

import java.util.List;
import java.util.Map;

/**
 * table Ù–‘
 * @author π∂Œ∞
 */
public class Table {
	private String tableName;
	private String forkeyName;
	private List<PrmiryKey> prmiryKey;
	private Map<String,Class<?>> propertyName;  //¡–√˚ value:property type
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getForkeyName() {
		return forkeyName;
	}
	public void setForkeyName(String forkeyName) {
		this.forkeyName = forkeyName;
	}

	public Map<String, Class<?>> getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(Map<String, Class<?>> propertyName) {
		this.propertyName = propertyName;
	}
	public List<PrmiryKey> getPrmiryKey() {
		return prmiryKey;
	}
	public void setPrmiryKey(List<PrmiryKey> prmiryKey) {
		this.prmiryKey = prmiryKey;
	}
	
	
}
