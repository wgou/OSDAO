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
 * ���ڶ�̬����ʵ�����ݿ����ӳ�
 * �������ӳ��Խ�����
 * @author ��ΰ
 *
 */
public class ConnectionPoolExcutor {
	/**
	 * ��ʼ�����Ӹ��������ӳ������ٻ����
	 */
	private volatile int  corePoolSize; 
	private static int defaultCorePoolSize = 5;
	/**
	 * ��������������ӳ��������ڻ���Ӹ���
	 */
	private volatile int  maxPoolSize; 
	private static int defaultMaxPoolSize = 15;
	/**
	 * ���ʱ���ӣ����ӳ��л�������趨keepAliveTimeʱ���ڣ�δ��ʹ������������
	 * �����ӳ���Ȼ����corePoolSize�����Ӳ������١�
	 */
	private volatile long  keepAliveTime;
	
	private static int defaultKeepAliveTime = 3;

	private static Lock lock = new ReentrantLock();
	//��ȡ������
	private static Condition takeLock = lock.newCondition();
	/**
	 * �������Map
	 * 		key:    Connection����
	 * 	    value:  ����״̬  ConnStauts
	 */
	private static  Map<MyIC,Status> map = new HashMap<MyIC, Status>(); 
	
	/**
	 * ������ӿ���ʱ��
	 */
	private static  Map<MyIC,Long> keep = new HashMap<MyIC, Long>();
	
	/**
	 * ���ӳ�ʹ�ñ��
	 */
	private static volatile boolean flag = true;
	
	
	public ConnectionPoolExcutor(int corePoolSize,
								 int maxPoolSize,
								 long keepAliveTime,
								 TimeUnit unit
								 ) throws SQLException{
		if(corePoolSize < 0)
				throw new AbsException("���ӳغ�������������С��0");
		if(corePoolSize > maxPoolSize)
				throw new AbsException("���ӳغ������������ܲ��ܴ������������");
		if(maxPoolSize < 0)
			throw new AbsException("���ӳ��������������С��0");
		if(keepAliveTime < 0)
			throw new AbsException("���ӳ����ӿ���ʱ�䲻��С��0");
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
	 * Ĭ�ϴ������ӳ�
	 * @throws SQLException 
	 */
	public ConnectionPoolExcutor() throws SQLException{
		this(defaultCorePoolSize,defaultMaxPoolSize);
	}
	//��ʼ������������
	private final void init() throws SQLException{
		int count = 0; 
		while(count < corePoolSize && corePoolSize < maxPoolSize){
			MyIC conn = MyConnection.getConnection(); //��̬����
			//Connection conn = new MyConnectionStaticProxy(); //��̬����
			map.put(conn, Status.ACTIVE);
			++ count;
			System.out.println("��ʼ����"+ count + "������!");
			keep.put(conn, System.currentTimeMillis());
		}
	}
	/**
	 * ���ӳ�ʹ�����ӳ������������������������Ӽ������ӳ�
	 * @throws SQLException 
	 */
	private final MyIC excutor() throws SQLException{
		MyIC conn = MyConnection.getConnection(); //��̬����
		//Connection conn = new MyConnectionStaticProxy();  //��̬����
		return conn;
	} 
	/**
	 * ��ȡ����
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
	public MyIC take() throws InterruptedException, SQLException{
		try{
			lock.lock();
			if(map.size() == maxPoolSize  ){//���ӳ��޿�������
				flag = false;
				while(!flag){
				//	System.out.println("~~~~(>_<)~~~~ ���ӱ���ȡ����~~");
					takeLock.await();
				}
			}
			MyIC conn = null;
			Iterator<Entry<MyIC, Status>> it = map.entrySet().iterator();
			while(it.hasNext()){
				Entry<MyIC, Status> entry = it.next();
				if(entry.getValue() == Status.ACTIVE){  //������ӳ��еĻ����
					conn = entry.getKey();
					map.put(conn, Status.INACTIVE);
					keep.put(conn, System.currentTimeMillis());
				//	System.out.println("ȡ����һ������"+print());
					break;
				}
			}
			if(conn == null){//����һ�����������ӳأ������������ӳ�
				conn = excutor(); 
				//System.out.println("�´�����һ�����ӣ����������!"+print());
				map.put(conn, Status.INACTIVE);
				keep.put(conn, System.currentTimeMillis());
				//System.out.println("ȡ����һ������"+print());
			}
			return conn;
		}finally{
			lock.unlock();
		}
	}
	/**
	 * ���ӻ��������ӳ�
	 */
	public final static void put(MyIC conn){
		try{
			lock.lock();
			map.put(conn, Status.ACTIVE);
			keep.put(conn, System.currentTimeMillis());
			//System.out.println("�Ż���һ������,"+print());
			flag = true;  
			takeLock.signal();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * ���ݿ���ճ�ʱ����
	 * @throws SQLException 
	 */
	private void evictionConnection() {
		Timer t =  new Timer();
		t.schedule(new RunTaskTimer(), 5000, 3*1000);
		//t.schedule(new SelectTimer(), 0, 2*1000);
	}
	
	/***
	 * ���ӳ��Լ����
	 * �����ӳ��еĿ������Ӵ�����С������ʱ
	 * ÿ1����һ�����ӳ��еĿ������ӡ�
	 * ���������ӵĿռ�ʱ�� ���� ����ռ�ʱ�䣬���Ƴ��أ��Ͽ����ݿ�����
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
						if(entry.getValue() == Status.ACTIVE){  //������ӳ��еĻ����
							MyIC conn = entry.getKey();
							long keepALiveTime_ = keep.get(conn);
							if((currentTime - keepALiveTime_) > keepAliveTime){
								if(count > corePoolSize){ //�Ƴ����ӳ�
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
			if(entry.getValue() == Status.ACTIVE)  //������ӳ��еĻ����
				++ i;
			else
				++ j;
		}
		return "���ӳ��й�["+map.size()+"]�����ӣ�����������["+i+"],����ʹ��������["+j+"]";
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
