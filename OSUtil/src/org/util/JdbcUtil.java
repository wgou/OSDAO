package org.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {
	private String driver;
	private String url;
	private String name;
	private String password;
	private String jarPath;
	
	public JdbcUtil(String driver, String url, String name, String password,String jarPath) {
		this.driver = driver;
		this.url = url;
		this.name = name;
		this.password = password;
		this.jarPath = jarPath;
	}
	public JdbcUtil(){}
	private final static ThreadLocal<Connection> td = new ThreadLocal<Connection>();
	//获取数据库连接
	public  final Connection getInitConnection() throws  SQLException, ClassNotFoundException {
		ExtClasspathLoader.loadClasspath(jarPath);
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, name, password);
		td.set(conn);
		return conn;
	}
	
	public  final Connection getConnection() throws SQLException {
		Connection conn = td.get();
		if(conn != null ) return conn;
		try {
			conn = getInitConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		td.set(conn);
		return conn;
	}
	
	public static final Connection conn(){
		return  td.get();
	}
	//关闭数据库连接
	public final void close(ResultSet rs, Statement stmt, Connection conn){
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
        if(conn != null){
        	td.remove();
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
}
