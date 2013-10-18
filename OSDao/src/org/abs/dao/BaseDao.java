package org.abs.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.abs.common.PagedList;
import org.abs.common.SQL;
import org.abs.jdbc.DBUtil;
import org.apache.commons.lang.StringUtils;

/*************************************
 * 数据访问功能类
 * 项 目 名 称: AbsDao
 * 文    件   名: ProxyFactory.java
 * 标	题: 
 * 描	述: 
 * 作	者：  苟伟
 * 创 建 时 间: 2013-10-6 23:02
 * 版	本: V1.0 
 * *************************************/
public abstract class BaseDao extends AbsDao{
	/**
	 * 数据库插入实体bean对象
	 * @param obj 
	 * @return 主键值
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws RuntimeException 
	 * @throws SQLException 
	 */
	public  Serializable insert(Object obj) throws Exception {
		Serializable serializable = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			SQL sql = this.buildInsetSql(obj);
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql.getSql());
			BaseDaoAssist.setSqlParams(pstm, sql.getParamsValue());
			pstm.executeUpdate();
			if(StringUtils.isNotEmpty((String)sql.getPrimaryKeyValue())){
				serializable = sql.getPrimaryKeyValue();
			}else{
				rs = pstm.getGeneratedKeys();
				if(rs != null && rs.next())
					serializable = rs.getString(1);
			}
		} catch (Exception e) {
				throw e;
		}finally{
			DBUtil.close(rs, pstm, conn);
		}
		return serializable;
	}
	/**
	 * 数据库插入实体bean对象
	 * @param obj 
	 * @return 主键值
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws RuntimeException 
	 * @throws SQLException 
	 */
	public  Serializable[] insertBatch(List<Object> objs) throws Exception {
		if(objs == null) return new Serializable[]{};
		if(objs.size() == 0) return new Serializable[]{};
		Serializable[] serializable =new String[objs.size()];
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			int i = 0;
			SQL sql = this.buildInsetSql(objs.get(0));
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);
			pstm = conn.prepareStatement(sql.getSql());
			Map<Serializable[],Object[][]> paramMap = this.getBathParam(objs);
			for(Map.Entry<Serializable[],Object[][]> params : paramMap.entrySet()){
				BaseDaoAssist.setSqlParams(pstm, params.getValue());
				serializable = params.getKey();
			}
			if(serializable.length == 0){
				rs = pstm.getGeneratedKeys();
				while(rs != null && rs.next())
				serializable[i++] = rs.getString(1);
			}
			pstm.executeBatch();
			conn.commit();
		} catch (Exception e) {
				throw e;
		}finally{
			DBUtil.close(rs, pstm, conn);
		}
		return serializable;
	}

	/**
	 * sql方式数据库新增数据
	 * @param sql
	 * @param params
	 * @return 主键值
	 * @throws Exception 
	 */
	public  int insertBySQL(String sql,Object...params) throws SQLException{
		int result = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm, params);
			result  = pstm.executeUpdate();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	}
	/**
	 * 采用sql语句批量新增，修改数据
	 * @param sql
	 * @param params new Object[][]{{},{}}
	 * @return
	 * @throws SQLException
	 */
	public int[] insertOrUpdateBatchSql(String sql,Object[][] params) throws SQLException{
		Connection conn = null;
		PreparedStatement pstm = null;
		int[] result = null;
		try{
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			if(params != null && params.length > 0){
				BaseDaoAssist.setSqlParams(pstm, params);
			}
			result = pstm.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	}
	
	/**
	 * 根据主键删除数据库对应数据
	 * @param clazz
	 * @param primaryKey值
	 * @return 删除条目
	 * @throws Exception 
	 */
	public  int delete(Class<?> clazz,Serializable id) throws Exception{
		int result = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(this.buildDelteSQL(clazz));
			BaseDaoAssist.setSqlParams(pstm, new Object[]{id});
			result  = pstm.executeUpdate();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	}
	/**
	 * 根据sql删除数据库对应数据
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public  int delete(String sql,Object...params) throws SQLException{
		int result = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm, params);
			result  = pstm.executeUpdate();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	} 
	/**
	 * 根据对象修改数据库对应数据
	 * @param obj
	 * @param primarykey
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public  int update(Object obj) throws IllegalArgumentException, IllegalAccessException, Exception{
		int result = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			SQL sql = this.buildUpdateSQL(obj);
			conn = DBUtil.getConnection();
			Serializable pkValue = sql.getPrimaryKeyValue();
			sql.getParamsValue().remove(pkValue);
			sql.getParamsValue().add(pkValue);
			pstm = conn.prepareStatement(sql.getSql());
			BaseDaoAssist.setSqlParams(pstm, sql.getParamsValue());
			result  = pstm.executeUpdate();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	}
	
	/**
	 * 根据sql修改数据库数据
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public  int update(String sql,Object...params) throws SQLException{
		int result = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm, params);
			result  = pstm.executeUpdate();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return result;
	}
	/**
	 * 批量修改数据库数据
	 * @param objs
	 * @return
	 * @throws Exception 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public  Serializable[] updateBatch(List<Object> objs) throws IllegalArgumentException, IllegalAccessException, Exception{
		Serializable[] serializable = new Serializable[objs.size()];
		if(objs.size() == 0) return serializable;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			SQL sql = this.buildUpdateSQL(objs.get(0));
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql.getSql());
			Map<Serializable[],Object[][]> paramMap = this.getBathParam(objs);
			for(Map.Entry<Serializable[],Object[][]> params : paramMap.entrySet()){
				BaseDaoAssist.setSqlParams(pstm, params.getValue());
				serializable = params.getKey();
			}
			pstm.executeBatch();
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return serializable;
	}
	/**
	 *  根据主键id查询实体对象
	 * @param clazz
	 * @param primaryKey
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public  <T>T queryById(Class<T> clazz,Serializable ids,String ...columnNames) throws Exception{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		T t = null;
		try {
			String sql = this.buildQueryByIdSQL(clazz,columnNames);
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm,new Object[]{ ids});
			rs = pstm.executeQuery();
			if (rs.next()) {
				t = this.getResultSetToBean(clazz,rs);
			}
		} catch (SQLException e) {
				throw e;
		}finally{
			DBUtil.close(null, pstm, conn);
		}
		return t;
	}
	/**
	 * sql查询返回List<Map>集合
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public  List<Map<String,Object>> queryDataMap(String sql,Object ...params) throws SQLException{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm, params);
			rs = pstm.executeQuery();
			if(rs != null) return this.getResultListMap(rs);
		} catch (SQLException e) {
			throw e;
		}finally{
			DBUtil.close(rs, pstm, conn); 
		}
		return null;
	}
	/**
	 * sql查询数据库，返回list集合
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public  <T>List<T> queryDataList(Class<T> clazz,String sql,Object...params) throws Exception{
		List<T> dataList = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			BaseDaoAssist.setSqlParams(pstm, params);
			rs = pstm.executeQuery();
			if(rs != null){
				dataList = new ArrayList<T>();
				while(rs.next()){
					T t = this.getResultSetToBean(clazz,rs);
					dataList.add(t);
				}
			}
		} catch (SQLException e) {
			throw e;
		}finally{
			DBUtil.close(rs, pstm, conn); 
		}
		return dataList;
	}
	/**
	 * 分页查询 
	 * @param clazz 查询对象
	 * @param sql 
	 * @param pageNum  当前页
	 * @param pageSize 每页条数
	 * @param params 参数
	 * @return
	 * @throws Exception 
	 */
	public <T>PagedList<T> getPagedListData(Class<T> clazz,String sql,int pageNum,int pageSize,Object...params) throws Exception{
		 int totalCount = this.queryTotalCountBySql(sql, params);
        PagedList<T> pl = new PagedList<T>(null, pageNum, pageSize, totalCount);
        if (totalCount < 1 || pl.getStart() >= totalCount || pl.getEnd() <= 0) {
            
        } else if (totalCount <= pl.getPageSize()) {
            pl.setDateList(this.queryDataList(clazz, sql, params));
        } else {
            pl.setDateList(this.queryDataList(clazz, getPagedListSQL(sql, pl.getStart(),pl.getEnd()), params));
        }
        return pl;
	}
	/**
	 * sql查询数据总条数
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public  int queryTotalCountBySql(String sql,Object... params) throws SQLException{
		int result  = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(this.getCountSQL(sql));
			BaseDaoAssist.setSqlParams(pstm, params);
			rs = pstm.executeQuery();
			if(rs != null && rs.next()){
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw e;
		}finally{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DBUtil.close(rs, pstm, conn); 
		}
		return result;
	}
	
}
