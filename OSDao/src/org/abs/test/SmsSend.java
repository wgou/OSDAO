package org.abs.test;
import org.abs.annotation.Table;
import org.abs.annotation.Column;
import org.abs.annotation.PrimaryKey;
import org.abs.annotation.KeyType;
@Table(name = "SMS_SEND")
public class SmsSend{
private String orgaddr;	private String mobile;	private String content;	private int id;	@Column(name="ID",type=@PrimaryKey(type=KeyType.NULL,name=""))
public int getId(){
     return id;	}	@Column(name="MOBILE")
public String getMobile(){
     return mobile;	}	@Column(name="ORGADDR")
public String getOrgaddr(){
     return orgaddr;	}	@Column(name="CONTENT")
public String getContent(){
     return content;	}	public void setMobile(String mobile ){
	 this. mobile = mobile;	}	public void setOrgaddr(String orgaddr ){
	 this. orgaddr = orgaddr;	}	public void setContent(String content ){
	 this. content = content;	}	public void setId(int id ){
	 this. id = id;	}	}