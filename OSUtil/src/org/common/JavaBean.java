package org.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * ���javaBean����
 * @author ��ΰ
 *
 */
public class JavaBean {
	private String pageName; //����
	private List<String> importPage; //����İ�
	private String className; //��
	private Map<String,String> property; //����k:���� v:��������
	private Map<String,Map<String,Map<String,String>>> getMethod; //K:����K:���� k:get������ v:��������
	private Map<String,Map<String,String>>  setMethod; //K:set������ k:���� v:��������
	private List<PrmiryKey> pkName; //����
	/** ע�� **/
	private String tableName; //tableע��
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public List<String> getImportPage() {
		return importPage;
	}
	public void setImportPage(List<String> importPage) {
		this.importPage = importPage;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Map<String,String> getProperty() {
		return property;
	}
	public void setProperty(Map<String,String> property) {
		this.property = property;
	}
	public Map<String,Map<String,Map<String,String>>> getGetMethod() {
		return getMethod;
	}
	public void setGetMethod(Map<String,Map<String,Map<String,String>>> getMethod) {
		this.getMethod = getMethod;
	}
	public Map<String,Map<String,String>> getSetMethod() {
		return setMethod;
	}
	public void setSetMethod(Map<String,Map<String,String>> setMethod) {
		this.setMethod = setMethod;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public List<PrmiryKey> getPkName() {
		return pkName;
	}
	public void setPkName(List<PrmiryKey> pkName) {
		this.pkName = pkName;
	}
	@Override
	public String toString() {
		StringBuffer javaBeanBuffer = new StringBuffer();
		if(getPageName() != null && getPageName().length() > 0){
			javaBeanBuffer.append("package ").append(getPageName()).append(";").append("\r\n");  //�����
		}
		if(getImportPage() != null && getImportPage().size() > 0){
			for(String imp : getImportPage()){
				javaBeanBuffer.append("import ").append(imp).append(";").append("\r\n");  //�����
			}
		}
		javaBeanBuffer.append("@Table(name = \"").append(getTableName()).append("\")").append("\r\n");//tableע��
		javaBeanBuffer.append("public class ").append(getClassName()).append("{").append("\r\n"); //д��className
		
		if(getProperty() != null && getProperty().size() > 0){
			for(Map.Entry<String, String> property : getProperty().entrySet()){
				javaBeanBuffer.append("private ").append(property.getValue()).append(" ").append(property.getKey()).append(";").append("\r\t");  //д������
			}
		}
		
		if(getGetMethod() != null && getGetMethod().size() > 0){
			Map<String,Map<String,Map<String,String>>> getMap = getGetMethod();
			List<String> removeKey = new ArrayList<String>();
			for(Map.Entry<String, Map<String,Map<String,String>>> methodTableBean : getMap.entrySet()){
				for(Map.Entry<String, Map<String,String>> methodBean : methodTableBean.getValue().entrySet()){
					for(Map.Entry<String, String> method : methodBean.getValue().entrySet()){
						if(getPkName() != null && getPkName().size() > 0){
							for(PrmiryKey pk : getPkName()){
								if(methodTableBean.getKey().equals(pk.getPrimaryKeyName())){//�÷���Ϊ��������
									//�������ע��
									javaBeanBuffer.append("@Column(name=\"").append(methodTableBean.getKey()).append("\",type=@PrimaryKey(type=").append("KeyType.NULL").append(",name=\"\"").append("))").append("\r\n");
									javaBeanBuffer.append("public ").append(method.getValue()).append(" ").append(method.getKey()).append("(){").append("\r\n");  //д��get����
									javaBeanBuffer.append("     return ").append(methodBean.getKey()).append(";").append("\r\t").append("}").append("\r\t");
									removeKey.add(methodTableBean.getKey());
								}
							}
						}
					}
				}
			}
			if(removeKey.size() > 0){
				for(String key : removeKey){
					System.out.println(key);
					getMap.remove(key); //�Ƴ���������
				}
			}
			//�����������get����
			for(Map.Entry<String, Map<String,Map<String,String>>> methodTableBean : getMap.entrySet()){
				for(Map.Entry<String, Map<String,String>> methodBean : methodTableBean.getValue().entrySet()){
					for(Map.Entry<String, String> method : methodBean.getValue().entrySet()){
						javaBeanBuffer.append("@Column(name=\"").append(methodTableBean.getKey()).append("\")").append("\r\n");
						javaBeanBuffer.append("public ").append(method.getValue()).append(" ").append(method.getKey()).append("(){").append("\r\n");  //д��get����
						javaBeanBuffer.append("     return ").append(methodBean.getKey()).append(";").append("\r\t").append("}").append("\r\t");
					}
				}
			}
		}
		if(getSetMethod()!= null && getSetMethod().size() > 0){
			for(Map.Entry<String, Map<String,String>> methodBean : getSetMethod().entrySet()){
				for(Map.Entry<String, String> method : methodBean.getValue().entrySet()){
					javaBeanBuffer.append("public void ").append(methodBean.getKey()).append("(").append(method.getValue()).append(" ").append(method.getKey()).append(" ){").append("\r\n");  //д��set����
					javaBeanBuffer.append("	 this. ").append(method.getKey()).append(" = ").append(method.getKey()).append(";").append("\r\t").append("}").append("\r\t");
				}
			}
		}
		javaBeanBuffer.append("}");
		return javaBeanBuffer.toString();
	}
	
}
