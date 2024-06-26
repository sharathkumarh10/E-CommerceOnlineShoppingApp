package com.jsp.osa.mapper;

import org.springframework.stereotype.Component;

import com.jsp.osa.entity.User;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.UserResponse;

import lombok.AllArgsConstructor;
@Component
@AllArgsConstructor
public class UserMapper {
	
	
	public User mapToUser(UserRequest userRequest, User user) {
		
		user.setEmail(userRequest.getEmail());
		user.setPassword((userRequest.getPassword()));
		
		
		return user;
		
	}

	public  UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}
}
