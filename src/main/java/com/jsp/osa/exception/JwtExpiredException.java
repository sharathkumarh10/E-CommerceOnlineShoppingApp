package com.jsp.osa.exception;

public class JwtExpiredException extends RuntimeException {
	
	String message;

	public JwtExpiredException(String message) {
		super();
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}

}
