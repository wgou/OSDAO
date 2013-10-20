package org.abs.jdbc;
/**
 * 连接池中，连接状态
 * @author 苟伟
 *
 */
public enum Status {
	ACTIVE,  //活动可用连接
	INACTIVE,//正在使用连接
}
