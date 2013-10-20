package org.common;


/**
 * 数据库配置
 * @author 苟伟
 *
 */
public class ConfigJdbc {
	private String driver;
	private String url;
	private String name;
	private String pass;
	private String min;
	private String max;
	private String maxTime;
	
	private String jndi;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}

	public String getJndi() {
		return jndi;
	}

	public void setJndi(String jndi) {
		this.jndi = jndi;
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("jdbc.driver=").append(getDriver()).append("\r\t");
		buffer.append("jdbc.url=").append(getUrl()).append("\r\t");
		buffer.append("jdbc.name=").append(getName()).append("\r\t");
		buffer.append("jdbc.pass=").append(getPass()).append("\r\t");
		buffer.append("############## ABS连接池 ###############\r\t");
		buffer.append("#最小连接数#\r\t");
		buffer.append("#minmum.connection.count=").append("").append("\r\t");
		buffer.append("#最大连接数#\r\t");
		buffer.append("#maxmum.connection.count=").append("").append("\r\t");
		buffer.append("#连接空闲时间(单位秒)#\r\t");
		buffer.append("#max.active.ime=").append("").append("\r\t");
		buffer.append("############# JNDI NAME #############\r\t");
		buffer.append("#jdbc.jndi=").append("").append("\r\t");
		return buffer.toString().trim();
	}
}
