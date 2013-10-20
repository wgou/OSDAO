package org.table;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.common.ConfigJdbc;
import org.common.JavaBean;
import org.common.PrmiryKey;
import org.common.Table;
import org.util.ClassUtil;
import org.util.FileUtil;
import org.util.TableUtil;
import org.util.Tools;

/**
 * ���û���ѡtable���ɿ���ʵ��bean
 * @author ��ΰ
 *
 */
public class TableConvertBean {
	private static TableUtil tableUtil = new TableUtil();
	private static final String annotationColumn = "org.abs.annotation.Column";
	private static final String annotationKeyType = "org.abs.annotation.KeyType";
	private static final String annotationPrimaryKey = "org.abs.annotation.PrimaryKey";
	private static final String annotationTable = "org.abs.annotation.Table";
	/**
	 * table convert bean
	 * @param tableNames ��������
	 * @param javaPath �����ļ�·��
	 * @param pageName ����java�ļ�����
	 * @throws SQLException
	 * @throws IOException
	 */
	public  int convertBean(List<String> tableNames,String javaPath,String pageName) throws SQLException, IOException{
		List<Table> tableList = new ArrayList<Table>();
		for(String bean : tableNames){
			 //����tableName��ѯtable��ϸ��Ϣ
			Map<String,String>  mapTable = tableUtil.getTableColumn(bean);
			Map<String,Class<?>> map = ClassUtil.columnConverMap(mapTable);
			List<String> pkNames = tableUtil.getPrimaryKeyName(bean);
			Table table = new Table();
			if(pkNames.size() > 0) {
				List<PrmiryKey> pkList = new ArrayList<PrmiryKey>(); //�����������ܴ�����������
				for(String pkName : pkNames){
					PrmiryKey pk = new PrmiryKey();
					Class<?> pkType = map.get(pkName);
					pk.setPrimaryKeyName(pkName);//����  
					pk.setPrimaryKeyType(pkType);//����  ����
					pkList.add(pk);
					map.remove(pkName);
				}
				table.setPrmiryKey(pkList);
			}
			table.setTableName(bean); //����
			table.setPropertyName(map);//������ - ��������
			//���
			tableList.add(table);
		}
		return createJavaBean(tableList,javaPath,pageName);
	}
	
	/**
	 * ת��ΪjavaBeanʵ�����
	 * @param tableList table���󼯺�
	 * @param javaPath 
	 * @param pageName
	 * @return
	 * @throws IOException
	 */
	private static int createJavaBean(List<Table> tableList,String javaPath,String pageName) throws IOException{
		int count  = 0;
		for(Table table : tableList){
			 List<String> importList = new ArrayList<String>();
			 importList.add(annotationTable);
			 importList.add(annotationColumn);
			 importList.add(annotationPrimaryKey);
			 importList.add(annotationKeyType);
			 Map<String,String> propertyMap = new HashMap<String, String>();
			 Map<String,Map<String,Map<String,String>>> getMehtodMapBean = new HashMap<String, Map<String,Map<String,String>>>();
			 Map<String,Map<String,String>> setMehtodMap = new HashMap<String, Map<String,String>>();
			if(table.getPrmiryKey() != null){
				for(PrmiryKey pk : table.getPrmiryKey()){
					String imp = Tools.checkImport(pk.getPrimaryKeyType());
					String pName = Tools.columnNameConvertPropertyName(pk.getPrimaryKeyName());
					String pType = Tools.propertyType(pk.getPrimaryKeyType().getName());
					String getName = Tools.getMethodName(pName,pk.getPrimaryKeyType());
					String setName = Tools.setMethodName(pName);
					if(imp != null)
						importList.add(imp);
					propertyMap.put(pName,pType);
					Map<String,String> getM = new HashMap<String, String>(1);
					getM.put(getName, pType);
					Map<String,Map<String,String>> getMehtodMap = new HashMap<String, Map<String,String>>();
					getMehtodMap.put(pName, getM);
					
					getMehtodMapBean.put(pk.getPrimaryKeyName(), getMehtodMap);
					
					Map<String,String> setM = new HashMap<String, String>(1);
					setM.put(pName, pType);
					setMehtodMap.put(setName, setM);
					
				}
			}
			for(Map.Entry<String, Class<?>> map : table.getPropertyName().entrySet()){
				String imp = Tools.checkImport(map.getValue());
				String pName = Tools.columnNameConvertPropertyName(map.getKey());
				String pType = Tools.propertyType(map.getValue().getName());
				String getName = Tools.getMethodName(pName,map.getValue());
				String setName = Tools.setMethodName(pName);
				if(imp != null)
					importList.add(imp);
				propertyMap.put(pName,pType);

				Map<String,String> getM = new HashMap<String, String>();
				getM.put(getName, pType);
				Map<String,Map<String,String>> getMehtodMap = new HashMap<String, Map<String,String>>();
				getMehtodMap.put(pName, getM);
				
				getMehtodMapBean.put(map.getKey(), getMehtodMap);
				
				Map<String,String> setM = new HashMap<String, String>();
				setM.put(pName, pType);
				setMehtodMap.put(setName, setM);
			}
			JavaBean javaBean = new JavaBean();
			javaBean.setPageName(pageName);
			javaBean.setImportPage(importList);
			javaBean.setClassName(Tools.tableNameConvertClassName(table.getTableName()));
			javaBean.setProperty(propertyMap);
			javaBean.setGetMethod(getMehtodMapBean);
			javaBean.setSetMethod(setMehtodMap);
			javaBean.setPkName(table.getPrmiryKey());
			javaBean.setTableName(table.getTableName());
			FileUtil.writeJavaFile(javaPath, javaBean.getClassName()+".java", javaBean.toString());
			++count;
		}
		return count;
	}
	//����abs.properties�ļ�
	public static int createAbsProperties(final String path,String driver,String url,String name , String pass){
		try {
			ConfigJdbc config = new ConfigJdbc();
			config.setDriver(driver);
			config.setUrl(url);
			config.setName(name);
			config.setPass(pass);
			FileUtil.writeJavaFile(path, "abs.properties", config.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
}
