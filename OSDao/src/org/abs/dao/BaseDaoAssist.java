package org.abs.dao;

import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import org.abs.annotation.Column;
import org.abs.annotation.KeyType;
import org.abs.common.UUID;
import org.abs.jdbc.DBUtil;
/*************************************
 * 反射构建SQL工具类
 * 项 目 名 称: AbsDao
 * 文    件   名: BaseDaoAssist.java
 * 标	题: 
 * 描	述: 
 * 作	者：  苟伟
 * 创 建 时 间: 2013-10-7 15:34
 * 版	本: V1.0 
 * *************************************/
public class BaseDaoAssist {
	//获取主键值
		public static String getprimaryKeyValue(Method method,String tableName) throws SQLException{
			Column cloumn = method.getAnnotation(Column.class);
			KeyType keyType = cloumn.type().type();
			String key = null;
			switch (keyType) {
			case UUID:
				key = UUID.getUUID32();
				break;
			case SEQUENCE:
				key = getSequenceValue(cloumn.type().name());
				break;
			case MAX:
				key = getMaxValue(tableName,cloumn.name());
				break;
			default:
				break;
			}
			return key;
			
		}
		//获取sequence的值
		private static String getSequenceValue(String seqName) throws SQLException{
			String seqValue = null;
			Connection conn = null;
			PreparedStatement pstm =null;
			ResultSet rs = null;
			try {
				conn = DBUtil.getConnection();
				pstm = conn.prepareStatement("select "+ seqName + ".nextval from dual");
				rs = pstm.executeQuery();
				if(rs.next()){
					seqValue = rs.getString(1);
				}
			} catch (SQLException e) {
				throw e;
			}finally{
				DBUtil.close(rs, pstm, conn);
			}
			return seqValue;
		}
		//获取最大值
		private static String getMaxValue(String tableName,String columnName) throws SQLException{
			String maxValue = null;
			Connection conn = null;
			PreparedStatement pstm =null;
			ResultSet rs = null;
			try {
				conn = DBUtil.getConnection();
				pstm = conn.prepareStatement("select max("+columnName+ ") from "+tableName);
				rs = pstm.executeQuery();
				if(rs.next()){
					maxValue = rs.getString(1);
				}
			} catch (SQLException e) {
				throw e;
			}finally{
				DBUtil.close(rs, pstm, conn);
			}
			return maxValue;
		}
		 public static void setSqlParams(PreparedStatement psmt,List<Object> params) throws SQLException{
			 if(params != null && params.size() > 0){  
				 for(int i = 0; i < params.size(); i++){
			            setSqlParam(psmt, i+1, params.get(i));
			        }
		     }
		   }
		 public static void setSqlParams(PreparedStatement psmt,Object[] params) throws SQLException{
			 if(params != null && params.length > 0){  
				 for(int i = 0; i < params.length; i++){
			            setSqlParam(psmt, i+1, params[i]);
			        }
		     }
		   }
		 public static void setSqlParams(PreparedStatement psmt,Object[][] params) throws SQLException{
			 if(params != null && params.length > 0){  
				 for(int i = 0; i < params.length; i++){
					 for(int j = 0; j < params[i].length; j++){
			            setSqlParam(psmt, j+1, params[i][j]);
					 	}
					 psmt.addBatch();
			        }
		     	}
		   }
		//  设置绑定变量值
	    public static void setSqlParam(PreparedStatement psmt, int index, Object value) throws SQLException{
	        if(value == null){
	            psmt.setString(index, null);
	        }else{
	            Class<?> clz = value.getClass();
	            if(clz == Integer.class || clz == int.class){
	            	psmt.setInt(index, (Integer)value);
	            }else if(clz == Date.class){
	                psmt.setDate(index, (Date) value);
	            }else if(clz == Timestamp.class){
	                psmt.setTimestamp(index, (Timestamp) value);
	            }else if(clz == java.util.Date.class){
	                java.util.Date d = (java.util.Date) value;
	                psmt.setTimestamp(index, new Timestamp(d.getTime()));
	            }else if (clz == Time.class){
	                psmt.setTime(index, (Time) value);
	            }else if(clz == Boolean.class || clz == Boolean.TYPE){
	                psmt.setBoolean(index, (Boolean)value);
	            }else if(clz == Clob.class){
	                psmt.setClob(index, (Clob)value);
	            }else{
	                psmt.setString(index, value.toString());
	            }
	        }        
	    }
	}
