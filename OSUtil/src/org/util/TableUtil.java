package org.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 根据用户填写，获取数据库表结构信息
 * @author 苟伟
 * 2013/10/10
 */
public class TableUtil extends JdbcUtil{
	
	//读取指定用户数据库所有表
	public  List<String> getTables() throws SQLException, ClassNotFoundException{
		List<String> tables = new ArrayList<String>();
		Connection conn = getConnection();
		PreparedStatement pstm = conn.prepareStatement("SELECT TABLE_NAME FROM USER_TABLES ");
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			tables.add(rs.getString(1));
		}
		return tables;
	}
	//查询指定表的列名和列类型
	
	public Map<String,String> getTableColumn(String tableName) throws SQLException{
		Map<String,String> tablesMap = new HashMap<String,String>();
		String sql = "SELECT T.COLUMN_NAME,T.DATA_TYPE FROM USER_TAB_COLUMNS T, USER_COL_COMMENTS C	 WHERE T.TABLE_NAME = C.TABLE_NAME AND T.COLUMN_NAME = C.COLUMN_NAME AND T.TABLE_NAME = ?" ;
		Connection conn = getConnection();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, tableName);
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			tablesMap.put(rs.getString("COLUMN_NAME"),rs.getString("DATA_TYPE"));
		}
		return tablesMap;
	}
	//查询table主键
	public List<String> getPrimaryKeyName(String tableName) throws SQLException{
		List<String> list = new ArrayList<String>();
		String sql = "SELECT UCC.COLUMN_NAME FROM USER_CONS_COLUMNS UCC , USER_CONSTRAINTS UC WHERE UCC.CONSTRAINT_NAME = UC.CONSTRAINT_NAME AND UC.CONSTRAINT_TYPE = 'P' AND UC.TABLE_NAME = ?";
		Connection conn = getConnection();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, tableName);
		ResultSet rs = pstm.executeQuery();
		while(rs.next()){
			list.add(rs.getString(1));
		}
		return list;
	}
	
}
