package com.jsp.osa.exception;

public class OtpExpiredException extends RuntimeException {
private String message;

public OtpExpiredException(String message) {
	super();
	this.message = message;
}

@Override
public String getMessage() {
	// TODO Auto-generated method stub
	return super.getMessage();
}


}
