package org.abs.test;
import org.abs.annotation.Table;
import org.abs.annotation.Column;
import org.abs.annotation.PrimaryKey;
import org.abs.annotation.KeyType;
@Table(name = "SMS_SEND")
public class SmsSend{
private String orgaddr;
public int getId(){
     return id;
public String getMobile(){
     return mobile;
public String getOrgaddr(){
     return orgaddr;
public String getContent(){
     return content;
	 this. mobile = mobile;
	 this. orgaddr = orgaddr;
	 this. content = content;
	 this. id = id;