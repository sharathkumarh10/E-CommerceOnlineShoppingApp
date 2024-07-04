package com.jsp.osa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenExpiredException extends RuntimeException {
	
	String message;

}
