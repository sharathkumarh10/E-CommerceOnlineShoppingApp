package com.jsp.osa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
	
	String message;

}
