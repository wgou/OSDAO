package org.abs.test;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.abs.annotation.Transaction;
import org.abs.common.PagedList;
import org.abs.dao.BaseDao;

public class TestInsert extends BaseDao{
	@Transaction
	public String insertBean() throws Exception{
		SmsSend sms = new SmsSend();
		sms.setMobile("18612455231");
		sms.setContent("test");
		sms.setOrgaddr("orgaddr");
		return (String) super.insert(sms);
	}
	public Serializable[] insertBathBean() throws Exception{
		SmsSend sms = new SmsSend();
		sms.setMobile("1861221521");
		sms.setContent("test");
		sms.setOrgaddr("orgaddr");
		SmsSend sms1 = new SmsSend();
		sms1.setMobile("18612415221");
		sms1.setContent("test1");
		sms1.setOrgaddr("orgaddr");
		SmsSend sms2 = new SmsSend();
		sms2.setMobile("1861258231");
		sms2.setContent("test2");
		sms2.setOrgaddr("orgaddr");
		List<Object> list = new ArrayList<Object>();
		list.add(sms);list.add(sms1);list.add(sms2);
		
		  Serializable[] ss = super.insertBatch(list);
		  for(Serializable s : ss){
			  System.out.println(s);
		  }
		  return ss;
				  
	}
	
	public int[] insertOrUpdateBatchSQL() throws SQLException{
		//String sql = "insert into sms_send (id,orgaddr,mobile,content)  values (?,?,?,?)";
		//int[] result =  super.insertOrUpdateBatchSql(sql, new Object[][]{{444,"5555","1861221631","testInsertBathSQL"},{446,"5555","1861221631","testInsertBathSQL"}});
		String sql = "update sms_send t set  t.orgaddr=? where t.id=?";
		int[] result =  super.insertOrUpdateBatchSql(sql, new Object[][]{{"999999",444},{"8888",446}});

		System.out.println("result:"+ result.length);
		return result;
	}
	public int insertSQL() throws Exception {
		String sql = "insert into sms_send t values (123,'orgaddrsql',18612215331,'testsql')";
		return (Integer) super.insertBySQL(sql);
	}
	@Transaction
	public int insertSQLParam() throws Exception {
		String sql = "insert into sms_send t values (?,?,?,?)";
		Object[] obj = new Object[]{134,"orgaddrsqlparams","18612215235","testsqlparams"};
		return (Integer) super.insertBySQL(sql,obj);
	}
	public int delete() throws Exception{
		return super.delete(SmsSend.class, 134);
	}
	public int delteBath() throws SQLException{
		String sql ="delete from sms_send t where t.id in (?,?,?)";
		Object[] obj = new Object[]{473,471,472};
		return super.delete(sql, obj);
		
	}
	public int update() throws SQLException{
		String sql = "update sms_send t set t.content = ?  where t.id = ?";
		return super.update(sql, new Object[]{"22222222",537});
	}
	public int updateBean() throws IllegalArgumentException, IllegalAccessException, Exception{
		SmsSend sms2 = new SmsSend();
		sms2.setId(537);
		sms2.setMobile("1234567890");
		sms2.setContent("test11111111111");
		sms2.setOrgaddr("orgaddr1111111111");
		return super.update(sms2);
		
	}
	public SmsSend queryById() throws Exception{
		return super.queryById(SmsSend.class, 537);
	}
	public List<Map<String,Object>> queryBySQLMap() throws SQLException{
		String sql  = "select * from sms_send t where t.id = ? and t.mobile = ?";
		return super.queryDataMap(sql,new Object[]{537,"1234567890"});
	}
	public List<SmsSend> queryBySQLList() throws Exception{
		String sql  = "select * from sms_send t ";
		return super.queryDataList(SmsSend.class, sql);
	}
	public int queryByCount() throws SQLException{
		String sql  = "select * from sms_send t where t.id = ?  ";
		return super.queryTotalCountBySql(sql,new Object[]{537});
	}
	public PagedList<SmsSend> getPagedListData() throws Exception{
		String sql ="select * from sms_send t ";
		PagedList<SmsSend> pagedList = super.getPagedListData(SmsSend.class, sql, 1, 10);
		List<SmsSend> data = pagedList.getDataList();
		for(SmsSend sms : data){
			System.out.println(sms.getId()+ " - " + "-"+sms.getMobile() + " -" + sms.getContent());
		}
		return pagedList;
	}
	public static void main(String[] args) {
		try {
		//proxyFactory bean事物操作
//			TestInsert test = ProxyFactory.getProxyClass(new TestInsert(), new Ihandler() {
//				public void end() {
//					System.out.println("结束事物调用了");
//				}
//				public void begin() {
//					System.out.println("开始事物了");
//				}
//			});
			//直接插入bean	new TestInsert().insertBean();
			//proxy sql插入 test.insertSQLParam();
			//sql 无参数插入 new TestInsert().insertSQL();
			//sql有参数插入 new TestInsert().insertSQLParam();
			///proxy删除   System.out.println(test.delete());
			//创建实例批量插入 new TestInsert().insertBathBean();
			//批量sql插入 和修改 new TestInsert().insertOrUpdateBatchSQL();
			//动态代理批量插入test.insertBathBean();
			//bean修改System.out.println(new TestInsert().updateBean());
			//sql修改 System.out.println(new TestInsert().update());
			//根据主键id查询对象 SmsSend send = new TestInsert().queryById();
			//查询sql返回list<Map>
			/*List<Map<String,Object>> data = new TestInsert().queryBySQLMap();
					for(Map<String,Object> map : data){
						for(Map.Entry<String, Object> m : map.entrySet()){
							System.out.println(m.getKey() +"  = "+ m.getValue());
						}
					}*/
			/*sql查询返回list<Object>
			 List<SmsSend> data = new TestInsert().queryBySQLList();
			  for(SmsSend sms : data){
				System.out.println(sms.getId() + " - " + sms.getMobile() + " - " + sms.getOrgaddr() + " - "+ sms.getContent());
			}*/
			//分页查询
			new TestInsert().getPagedListData();
			//sql查询总条数  连接池
			/*for(int i = 0;i<30;i++){
				new Thread(new Runnable() {
					public void run() {
						try {
						System.out.println(new TestInsert().queryByCount());
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			*/
	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
