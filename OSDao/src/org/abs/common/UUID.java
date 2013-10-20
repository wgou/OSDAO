package org.abs.common;
/**
 * UUID32生成�?
 * @author Administrator
 *
 */
public class UUID {
	public static String getUUID32(){
		String uuid = java.util.UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
	
	public static void main(String[] args) {
		getUUID32();
	}
}
