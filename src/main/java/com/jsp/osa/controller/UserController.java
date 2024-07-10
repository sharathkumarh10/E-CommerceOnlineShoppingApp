package com.jsp.osa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.osa.enums.UserRole;
import com.jsp.osa.requestdto.AuthRequest;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.AuthResponse;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.service.UserService;
import com.jsp.osa.utility.ResponseStructure;
import com.jsp.osa.utility.SimpleStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@RestController
@RequestMapping("/api/v3")
@AllArgsConstructor

public class UserController {

	private UserService userService;

	@PostMapping("/register/sellers")

	public ResponseEntity<ResponseStructure<UserResponse>> saveSeller(@Valid @RequestBody UserRequest userRequest,
			UserRole userRole) {
		return userService.saveUser(userRequest, UserRole.SELLER);

	}

	@PostMapping("/register/customers")
	public ResponseEntity<ResponseStructure<UserResponse>> saveCustomers(@Valid @RequestBody UserRequest userRequest,
			UserRole userRole) {
		return userService.saveUser(userRequest, UserRole.CUSTOMER);

	}

	@PostMapping("/users/otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(
			@RequestBody OTPVerificationRequest otpVerificationRequest) {
		return userService.verifyOtp(otpVerificationRequest);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
		return userService.login(authRequest);

	}

	@PostMapping("/refreshLogin")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(
			@CookieValue(value = "rt", required = false) String refreshToken) {
		return userService.refreshlogin(refreshToken);

	}

	@PostMapping("/test")
	public String test() {
		return "success";
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(
			@CookieValue(value = "rt", required = false) String refreshToken,
			@CookieValue(value = "at", required = false) String accessToken) {
		return userService.logout(refreshToken, accessToken);

	}

	@PostMapping("/logoutFromOtherDevices")

	public ResponseEntity<SimpleStructure> logoutFromOtherDevices(
			@CookieValue(value = "rt", required = false) String refreshToken,
			@CookieValue(value = "at", required = false) String accessToken) {
		return userService.logoutFromOtherDevices(refreshToken, accessToken);
	}

	@PostMapping("/logoutAll")
	public ResponseEntity<SimpleStructure> logoutFromAllDevices(@CookieValue(value="at",required=false) String accessToken) {
		return userService.logoutFromAllDevices(accessToken);

	}

}
