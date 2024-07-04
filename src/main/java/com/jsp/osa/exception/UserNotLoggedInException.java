package com.jsp.osa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserNotLoggedInException extends RuntimeException {
	
	String message;

}
