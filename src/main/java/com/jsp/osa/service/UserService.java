package com.jsp.osa.service;

import org.springframework.http.ResponseEntity;

import com.jsp.osa.enums.UserRole;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.utility.ResponseStructure;

public interface UserService {

	

	ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole seller);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OTPVerificationRequest otpVerificationRequest);

}
