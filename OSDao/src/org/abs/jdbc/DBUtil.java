package org.abs.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.abs.common.AbsException;
import org.abs.common.PropertiesUtil;
import org.apache.commons.lang.StringUtils;

/************************************
 * ��ȡ���ݿ����ӣ��������  
 * �� Ŀ �� ��: AbsDao
 * ��    ��   ��: DBUtil.java
 * ��	��: 
 * ��	��: 
 * ��	�ߣ�  ��ΰ
 * �� �� ʱ ��: 2013-10-6 23:37
 * ��	��: V1.0 
 * *************************************/
public class DBUtil {
	private static final ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	private static final ThreadLocal<Boolean> isTranscation = new ThreadLocal<Boolean>();
	private static final String JNDI_NAME = PropertiesUtil.getProp("jdbc.jndi");
	private static final String MIN_SIZE = PropertiesUtil.getProp("minmum.connection.count");
	private static final String MAX_SIZE = PropertiesUtil.getProp("maxmum.connection.count");
	private static final String ACTIVE_TIME = PropertiesUtil.getProp("max.active.ime");
	private static Context context = null;
	/**
	 * ���ӳ�ʵ��
	 */
	private static ConnectionPoolExcutor poolConnectionExcutor = null;
	static{
		if(poolConnectionExcutor == null  && StringUtils.isEmpty(JNDI_NAME)){
			try {
				poolConnectionExcutor = initPoolExcutor();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//��ʼ�����ӳ�
	private static ConnectionPoolExcutor initPoolExcutor() throws SQLException{
		if(StringUtils.isNotEmpty(MIN_SIZE) && StringUtils.isNotEmpty(MAX_SIZE)){
			int corePoolSize = Integer.parseInt(MIN_SIZE.trim());
			int maxPoolSize = Integer.parseInt(MAX_SIZE.trim());
			if(StringUtils.isNotEmpty(ACTIVE_TIME)){
				long  keepAliveTime = Long.parseLong(ACTIVE_TIME.trim());
				poolConnectionExcutor = new ConnectionPoolExcutor(corePoolSize,maxPoolSize,keepAliveTime,TimeUnit.MICROSECONDS);
			}else{
				poolConnectionExcutor = new ConnectionPoolExcutor(corePoolSize,maxPoolSize);
			}
		}else{
			poolConnectionExcutor = new ConnectionPoolExcutor();
		}
		return poolConnectionExcutor;
	}

	//��ȡ���ݿ�����
	public static Connection getConnection() throws SQLException{
		Connection conn = tl.get();
		if(JNDI_NAME == null){
			try {
				if(poolConnectionExcutor == null)
					throw new AbsException("��ʼ�����ӳ����쳣!");
				conn = poolConnectionExcutor.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
		  if(conn == null){
			if(context == null)
				try {
					context = getContext();
					DataSource dataSource = (DataSource)context.lookup(JNDI_NAME);
					conn = dataSource.getConnection();
				} catch (NamingException e) {
					e.printStackTrace();
				}
		   }
		}
		tl.set(conn);
		return conn;
	}
	//close conn
	public static void close(ResultSet rs, Statement stmt, Connection conn){
		 if(rs != null){
	            try {
	                rs.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        if(stmt != null){
	            try {
	            	stmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        Boolean boo = isTranscation.get();
	        if(boo == null || !boo){
	            tl.remove();
	            if(conn != null){
	                try {
	                    conn.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        }       
	}
	private static Context getContext() throws NamingException{
		return new InitialContext();
	}
	//��ʼ����
	public static void beginTranscation() throws SQLException{
		isTranscation.set(true);
		Connection conn = tl.get();
		if(conn == null) {
			conn = getConnection();
			tl.set(conn);
		}
		conn.setAutoCommit(false);
		System.out.println("beginTransaction");
	}
	//��������
	public static void endTranscation() throws SQLException{
		if(isTranscation.get() == null || !isTranscation.get()) throw new AbsException("call endTranscation before must call beginTranscation ");
		isTranscation.set(false);
		Connection conn = tl.get();
		conn.commit();
		System.out.println("endTranscation");
	}
	//����ع�
	public static void rollbackTranscation() throws SQLException{
		if(isTranscation.get() == null || !isTranscation.get()) throw new AbsException("call rollbackTranscation before must call beginTranscation ");
		isTranscation.set(false);
		Connection conn = tl.get();
		conn.rollback();
		System.out.println("rollbackTranscation");
	}
	
}
