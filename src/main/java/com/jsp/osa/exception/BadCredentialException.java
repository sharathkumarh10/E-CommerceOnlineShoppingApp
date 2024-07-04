package com.jsp.osa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadCredentialException extends RuntimeException {
	
	String message;
	

}
