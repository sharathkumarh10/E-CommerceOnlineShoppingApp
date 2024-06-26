package com.jsp.osa.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPVerificationRequest {
	private String email;
	private String otp;

}
