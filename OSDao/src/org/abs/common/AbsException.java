package org.abs.common;

public class AbsException extends RuntimeException {
	private static final long serialVersionUID = 3999287727003744629L;
	public AbsException(String e){
		super(e);
	}
	public AbsException(){
		super();
	}
}
