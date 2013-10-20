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
 * 动态代理类
 * jdk1.5基于接口实现类的动态代理和cglib动态代理
 * 用于处理数据库transcation操作和log记录
 * 项 目 名 称: AbsDao
 * 文    件   名: ProxyFactory.java
 * 标	题: 
 * 描	述: 
 * 作	者：  苟伟
 * 创 建 时 间: 2013-10-6 23:02
 * 版	本: V1.0 
 * *************************************/
public class ProxyFactory {
	@SuppressWarnings("unchecked")
	public static <T>T getProxyClass(final T clazz,final Ihandler handler){
		if(clazz.getClass().getInterfaces() != null && clazz.getClass().getInterfaces().length > 0){
			//采用jdk代理
			return (T)Proxy.newProxyInstance(clazz.getClass().getClassLoader(), new Class[]{Connection.class}, new InvocationHandler() {
				public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
						return ProxyFactory.invoke(clazz, method,args,handler);
				}
			});
		}else{
			//cglib代理
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
