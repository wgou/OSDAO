package org.abs.proxy.statics;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

import org.abs.common.PropertiesUtil;

public class MyConnectionStaticProxy implements Connection{
	private static final String JDBC_DRIVER = PropertiesUtil.getProp("jdbc.driver");
	private static final String JDBC_URL = PropertiesUtil.getProp("jdbc.url");
	private static final String JDBC_NAME = PropertiesUtil.getProp("jdbc.name");
	private static final String JDBC_PASS = PropertiesUtil.getProp("jdbc.pass");
	private  Connection conn;

	public Connection getConn(){
		return this.conn;
	}
	public  MyConnectionStaticProxy() throws SQLException{
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		 this.conn = DriverManager.getConnection(JDBC_URL, JDBC_NAME, JDBC_PASS);
	}
	
	//为连接池已满提供连接创建
	public static Connection newConnection() throws SQLException{
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(JDBC_URL, JDBC_NAME, JDBC_PASS);
	}
	
	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}
	public CallableStatement prepareCall(String sql) throws SQLException {
		return conn.prepareCall(sql);
	}
	public String nativeSQL(String sql) throws SQLException {
		return conn.nativeSQL(sql);
	}
	public void setAutoCommit(boolean autoCommit) throws SQLException {
	}
	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}
	public void commit() throws SQLException {
		 conn.commit();
	}
	public void rollback() throws SQLException {
		conn.rollback();
	}
	public void close() throws SQLException {
	//	ConnectionPoolExcutor.put(this);
	}
	public static void disClose(MyConnectionStaticProxy conn)throws SQLException {
		System.out.println("================= 开始close ================= ");
		conn.getConn().close();  
		System.out.println("================= 结束close =================");
	}
	
	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}
	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}
	public void setReadOnly(boolean readOnly) throws SQLException {
		conn.setReadOnly(readOnly);
	}
	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}
	public void setCatalog(String catalog) throws SQLException {
		conn.setCatalog(catalog);
	}
	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}
	public void setTransactionIsolation(int level) throws SQLException {
		conn.setTransactionIsolation(level);
	}
	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}
	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}
	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return conn.createStatement(resultSetType,resultSetConcurrency);
	}
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareStatement(sql,resultSetType,resultSetConcurrency);
	}
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareCall(sql,resultSetType,resultSetConcurrency);
	}
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		conn.setTypeMap(map);
	}
	public void setHoldability(int holdability) throws SQLException {
		conn.setHoldability(holdability);
	}
	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}
	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}
	public Savepoint setSavepoint(String name) throws SQLException {
		return conn.setSavepoint(name);
	}
	public void rollback(Savepoint savepoint) throws SQLException {
		conn.rollback();
	}
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		conn.releaseSavepoint(savepoint);
	}
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
	}
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
	}
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
	}
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return conn.prepareStatement(sql,autoGeneratedKeys);
	}
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return conn.prepareStatement(sql,columnIndexes);
	}
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return conn.prepareStatement(sql, columnNames);
	}

}
