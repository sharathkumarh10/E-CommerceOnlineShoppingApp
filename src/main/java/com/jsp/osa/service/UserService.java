package com.jsp.osa.service;

import org.springframework.http.ResponseEntity;

import com.jsp.osa.enums.UserRole;
import com.jsp.osa.requestdto.AuthRequest;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.AuthResponse;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.utility.ResponseStructure;
import com.jsp.osa.utility.SimpleStructure;

public interface UserService {

	

	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole userRole);

	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OTPVerificationRequest otpVerificationRequest);

	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest );

	public ResponseEntity<ResponseStructure<AuthResponse>> refreshlogin(String refreshToken);

	public ResponseEntity<ResponseStructure<AuthResponse>> logout(String refreshToken, String accessToken);


	ResponseEntity<SimpleStructure> logoutFromAllDevices();
}
