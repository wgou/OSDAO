package org.abs.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import org.abs.jdbc.MyIC;
import org.abs.jdbc.MyConnection;
/**
 * ��̬������Connection ����
 * Ŀ��Ϊ�˹���close����
 * @author wgou
 *
 */
public class JdbcProxy implements  InvocationHandler { /** Ŀ����� */  
    private Connection targetConnection;  
    /** ������� */  
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
        if("close".equals(method.getName())){ // ����close����  
            MyConnection.close(proxyConnection);  
            return null;  
        }  
        if("myclose".equals(method.getName())){ // ������close 
        	targetConnection.close();
            return null;  
        } 
        return method.invoke(targetConnection, args);  
    }              
 }
