package com.jsp.osa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.osa.enums.UserRole;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.service.UserService;
import com.jsp.osa.utility.ResponseStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@RestController
@RequestMapping("/api/v3")
@AllArgsConstructor
public class UserController {
	
	private UserService userService;
	@PostMapping("/register/sellers")
	
	public ResponseEntity<ResponseStructure<UserResponse>>saveSeller(@Valid @RequestBody UserRequest userRequest){
		return userService.saveUser(userRequest,UserRole.SELLER);
		 
	}
	@PostMapping("/register/customers")
	public ResponseEntity<ResponseStructure<UserResponse>>saveCustomers(@Valid @RequestBody UserRequest userRequest){
		return userService.saveUser(userRequest,UserRole.CUSTOMER);
		 
	}
	@PostMapping("/users/otp")
	public ResponseEntity<ResponseStructure<UserResponse>>verifyOtp(@RequestBody OTPVerificationRequest otpVerificationRequest){
		return userService.verifyOtp(otpVerificationRequest);
		
	}

}
