package com.jsp.osa.securityfilters;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsp.osa.utility.ErrorStructure;

import jakarta.servlet.http.HttpServletResponse;

public class TokenExceptionHandler {
	
		public void tokenException(int status,HttpServletResponse response,String rootCause) throws StreamWriteException, IOException {
			
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			
			ErrorStructure<String> error=new ErrorStructure<String>()
					.setStatus(HttpStatus.UNAUTHORIZED.value())
					.setMessage("Failed to authenticate")
					.setRootcause(rootCause);
			new ObjectMapper().writeValue(response.getOutputStream(), error);
		}

}
