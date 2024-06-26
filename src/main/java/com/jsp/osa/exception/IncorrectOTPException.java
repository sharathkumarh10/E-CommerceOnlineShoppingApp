package com.jsp.osa.exception;

public class IncorrectOTPException extends  RuntimeException {
private String message;

@Override
public String getMessage() {
	// TODO Auto-generated method stub
	return super.getMessage();
}

public IncorrectOTPException(String message) {
	super();
	this.message = message;
}

}
