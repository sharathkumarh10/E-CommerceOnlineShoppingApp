package com.jsp.osa.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
	
	private int userId;
	private String userName;
	private String roles;
	private long accessExpiration;
	private long refreshExpiration;
	

}
