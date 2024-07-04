package com.jsp.osa.exception;

public class InavlidJwtException extends RuntimeException {
	
	String message;

	public InavlidJwtException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	

}
