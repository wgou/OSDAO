package org.abs.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import org.abs.jdbc.MyIC;
import org.abs.jdbc.MyConnection;
/**
 * 动态代理创建Connection 连接
 * 目的为了过滤close方法
 * @author wgou
 *
 */
public class JdbcProxy implements  InvocationHandler { /** 目标对象 */  
    private Connection targetConnection;  
    /** 代理对象 */  
    private MyIC proxyConnection;  
      
   public JdbcProxy(Connection conn) {  
        targetConnection = conn;  
    }  
      
   public MyIC getProxy() {  
        proxyConnection = (MyIC)Proxy.newProxyInstance(MyIC.class.getClassLoader(),  new Class[]{MyIC.class},this);  
        return proxyConnection;  
    }  
    public Object invoke(Object proxy,Method method,Object[] args)  
                            throws Throwable {  
        if("close".equals(method.getName())){ // 拦截close方法  
            MyConnection.close(proxyConnection);  
            return null;  
        }  
        if("myclose".equals(method.getName())){ // 真正的close 
        	targetConnection.close();
            return null;  
        } 
        return method.invoke(targetConnection, args);  
    }              
 }
