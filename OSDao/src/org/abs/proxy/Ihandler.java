package org.abs.proxy;
/**
 * 调用方法之前和之后执行的操作
 * @author 苟伟
 *
 */
public interface Ihandler {
	void begin();
	void end();
}
