package org.abs.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.abs.annotation.Transaction;
import org.abs.jdbc.DBUtil;
/*************************************
 * ��̬������
 * jdk1.5���ڽӿ�ʵ����Ķ�̬�����cglib��̬����
 * ���ڴ������ݿ�transcation������log��¼
 * �� Ŀ �� ��: AbsDao
 * ��    ��   ��: ProxyFactory.java
 * ��	��: 
 * ��	��: 
 * ��	�ߣ�  ��ΰ
 * �� �� ʱ ��: 2013-10-6 23:02
 * ��	��: V1.0 
 * *************************************/
public class ProxyFactory {
	@SuppressWarnings("unchecked")
	public static <T>T getProxyClass(final T clazz,final Ihandler handler){
		if(clazz.getClass().getInterfaces() != null && clazz.getClass().getInterfaces().length > 0){
			//����jdk����
			return (T)Proxy.newProxyInstance(clazz.getClass().getClassLoader(), new Class[]{Connection.class}, new InvocationHandler() {
				public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
						return ProxyFactory.invoke(clazz, method,args,handler);
				}
			});
		}else{
			//cglib����
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(clazz.getClass());
			enhancer.setCallback(new MethodInterceptor() {
				public Object intercept(Object obj, Method method, Object[] args,MethodProxy methodProxy) throws Throwable {
					return ProxyFactory.invoke(clazz, method,args,handler);
				}
			});
			return (T)enhancer.create();
		}
	}
	
	private static Object invoke(Object obj,Method method,Object[] args,Ihandler handler ) throws Exception, IllegalAccessException, InvocationTargetException{
		Object result = null;
		boolean isTranscation = method.isAnnotationPresent(Transaction.class);
		if(isTranscation){
			try {
				DBUtil.beginTranscation();
				if(handler != null)
					handler.begin();
				result = (Object)method.invoke(obj, args);
				if(handler != null)
					handler.end();
				DBUtil.endTranscation();
			} catch (Exception e) {
				try {
					DBUtil.rollbackTranscation();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}finally{
				DBUtil.close(null, null, DBUtil.getConnection());
			}
		}else{
			if(handler != null)
				handler.begin();
			result = (Object)method.invoke(obj, args);
				if(handler != null)
			handler.end();
		}
		return result;
	}
}
