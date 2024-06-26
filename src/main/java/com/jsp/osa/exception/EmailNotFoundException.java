package com.jsp.osa.exception;

public class EmailNotFoundException extends RuntimeException {
private String message;

@Override
public String getMessage() {
	// TODO Auto-generated method stub
	return super.getMessage();
}

public EmailNotFoundException(String message) {
	super();
	this.message = message;
}

}
