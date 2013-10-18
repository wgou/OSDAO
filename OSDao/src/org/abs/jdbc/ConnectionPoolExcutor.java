package org.abs.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.abs.common.AbsException;
/**
 * 基于动态代理实现数据库连接池
 * 包含连接池自建机制
 * @author 苟伟
 *
 */
public class ConnectionPoolExcutor {
	/**
	 * 初始化连接个数，连接池中最少活动连接
	 */
	private volatile int  corePoolSize; 
	private static int defaultCorePoolSize = 5;
	/**
	 * 最大连接数，连接池中最多存在活动连接个数
	 */
	private volatile int  maxPoolSize; 
	private static int defaultMaxPoolSize = 15;
	/**
	 * 活动超时连接，连接池中活动连接在设定keepAliveTime时间内，未被使用则销毁连接
	 * 但连接池任然保留corePoolSize个连接不被销毁。
	 */
	private volatile long  keepAliveTime;
	
	private static int defaultKeepAliveTime = 3;

	private static Lock lock = new ReentrantLock();
	//获取连接锁
	private static Condition takeLock = lock.newCondition();
	/**
	 * 存放连接Map
	 * 		key:    Connection连接
	 * 	    value:  连接状态  ConnStauts
	 */
	private static  Map<MyIC,Status> map = new HashMap<MyIC, Status>(); 
	
	/**
	 * 存放连接空闲时间
	 */
	private static  Map<MyIC,Long> keep = new HashMap<MyIC, Long>();
	
	/**
	 * 连接池使用标记
	 */
	private static volatile boolean flag = true;
	
	
	public ConnectionPoolExcutor(int corePoolSize,
								 int maxPoolSize,
								 long keepAliveTime,
								 TimeUnit unit
								 ) throws SQLException{
		if(corePoolSize < 0)
				throw new AbsException("连接池核心连接数不能小于0");
		if(corePoolSize > maxPoolSize)
				throw new AbsException("连接池核心连接数不能不能大于最大连接数");
		if(maxPoolSize < 0)
			throw new AbsException("连接池最大连接数不能小于0");
		if(keepAliveTime < 0)
			throw new AbsException("连接池连接空闲时间不能小于0");
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveTime = unit.toNanos(keepAliveTime);
		System.out.println("keepAliveTime:"+ this.keepAliveTime);
		init();
		evictionConnection();
	}
	
	public ConnectionPoolExcutor(int corePoolSize,int maxPoolSize) throws SQLException{
		this(corePoolSize,maxPoolSize,defaultKeepAliveTime,TimeUnit.MICROSECONDS);
	}
	/**
	 * 默认创建连接池
	 * @throws SQLException 
	 */
	public ConnectionPoolExcutor() throws SQLException{
		this(defaultCorePoolSize,defaultMaxPoolSize);
	}
	//初始化核心连接数
	private final void init() throws SQLException{
		int count = 0; 
		while(count < corePoolSize && corePoolSize < maxPoolSize){
			MyIC conn = MyConnection.getConnection(); //动态代理
			//Connection conn = new MyConnectionStaticProxy(); //静态代理
			map.put(conn, Status.ACTIVE);
			++ count;
			System.out.println("初始化了"+ count + "个连接!");
			keep.put(conn, System.currentTimeMillis());
		}
	}
	/**
	 * 连接池使用连接超过核心连接数，创建新连接加入连接池
	 * @throws SQLException 
	 */
	private final MyIC excutor() throws SQLException{
		MyIC conn = MyConnection.getConnection(); //动态代理
		//Connection conn = new MyConnectionStaticProxy();  //静态代理
		return conn;
	} 
	/**
	 * 获取连接
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
	public MyIC take() throws InterruptedException, SQLException{
		try{
			lock.lock();
			if(map.size() == maxPoolSize  ){//连接池无空闲连接
				flag = false;
				while(!flag){
				//	System.out.println("~~~~(>_<)~~~~ 连接被你取完啦~~");
					takeLock.await();
				}
			}
			MyIC conn = null;
			Iterator<Entry<MyIC, Status>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<MyIC, Status> entry = it.next();
				if(entry.getValue() == Status.ACTIVE){  //检查连接池中的活动连接
					conn = entry.getKey();
					map.put(conn, Status.INACTIVE);
					keep.put(conn, System.currentTimeMillis());
				//	System.out.println("取走了一个连接"+print());
					break;
				}
			}
			if(conn == null){//创建一个连接至连接池，并保留至连接池
				conn = excutor(); 
				//System.out.println("新创建了一个连接，并放入池中!"+print());
				map.put(conn, Status.INACTIVE);
				keep.put(conn, System.currentTimeMillis());
				//System.out.println("取走了一个连接"+print());
			}
			return conn;
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 连接回收至连接池
	 */
	public final static void put(MyIC conn){
		try{
			lock.lock();
			map.put(conn, Status.ACTIVE);
			keep.put(conn, System.currentTimeMillis());
			//System.out.println("放回了一个连接,"+print());
			flag = true;  
			takeLock.signal();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 数据库回收超时连接
	 * @throws SQLException 
	 */
	private void evictionConnection() {
		Timer t =  new Timer();
		t.schedule(new RunTaskTimer(), 5000, 3*1000);
		//t.schedule(new SelectTimer(), 0, 2*1000);
	}
	
	/***
	 * 连接池自检机制
	 * 当连接池中的空闲连接大于最小连接数时
	 * 每1秒检查一次连接池中的空闲连接。
	 * 当空闲连接的空间时间 大于 允许空间时间，则移除池，断开数据库连接
	 * @return
	 */
	private class RunTaskTimer extends TimerTask{
		@Override
		public synchronized void run() {
			if(map.size() > corePoolSize){
				synchronized (map) {
					int count  = map.size();
					List<MyIC> temp = new ArrayList<MyIC>();
					long currentTime = System.currentTimeMillis();
					Iterator<Entry<MyIC, Status>> it = map.entrySet().iterator();
					while(it.hasNext()){
						Entry<MyIC, Status> entry = it.next();
						if(entry.getValue() == Status.ACTIVE){  //检查连接池中的活动连接
							MyIC conn = entry.getKey();
							long keepALiveTime_ = keep.get(conn);
							if((currentTime - keepALiveTime_) > keepAliveTime){
								if(count > corePoolSize){ //移除连接池
									keep.remove(conn);
									temp.add(conn);
									count -- ;
								}
							}
						}
					}
					for(MyIC conn : temp){
						try {
							conn.myclose();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						map.remove(conn);
					}
				}
			}
			//System.out.println(print()); 
		}
		
	}
	/*
	private static String print(){
		int i = 0;
		int j = 0;
		Iterator<Entry<MyIC, Status>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<MyIC, Status> entry = it.next();
			if(entry.getValue() == Status.ACTIVE)  //检查连接池中的活动连接
				++ i;
			else
				++ j;
		}
		return "连接池中共["+map.size()+"]个连接！空闲连接数["+i+"],正在使用连接数["+j+"]";
	}*/

	public int getCorePoolSize() {
		return corePoolSize;
	}
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	public long getKeepAliveTime(TimeUnit unit) {
		  return unit.convert(keepAliveTime, TimeUnit.MICROSECONDS);
	}
	public void setKeepAliveTime(long time, TimeUnit unit) {
		 if (time < 0)
	            throw new IllegalArgumentException();
		 if(time == 0)
			 	time = defaultKeepAliveTime;
	        this.keepAliveTime = unit.toNanos(time);
	}
}
