package com.jsp.osa.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
 
import com.jsp.osa.entity.Customer;
import com.jsp.osa.entity.Seller;
import com.jsp.osa.entity.User;
import com.jsp.osa.enums.UserRole;
import com.jsp.osa.exception.EmailNotFoundException;
import com.jsp.osa.exception.IncorrectOTPException;
import com.jsp.osa.exception.OtpExpiredException;
import com.jsp.osa.exception.UserAlreadyExistException;
import com.jsp.osa.mapper.UserMapper;
import com.jsp.osa.repository.CustomerRepository;
import com.jsp.osa.repository.SellerRepository;
import com.jsp.osa.repository.UserRepository;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.service.MailService;
import com.jsp.osa.service.UserService;
import com.jsp.osa.utility.MessageData;
import com.jsp.osa.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final CustomerRepository customerRepository;
	private final UserMapper userMapper;
	private final Cache<String, User> userCache;
	private final Random random;
	private final Cache<String, String>otpCache;
	private final MailService mailService;
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole userRole) {

	
		 if (userRepository.existsByEmail(userRequest.getEmail()))
	            throw new UserAlreadyExistException("User already exist");
			User user = null;
		MessageData messageData =new MessageData();


		switch (userRole) {
		case CUSTOMER -> user = new Customer();
		case SELLER -> user = new Seller();
		}
		if(user != null) {
			user = userMapper.mapToUser(userRequest, user);
			user.setUserRole(userRole);
		}

		int number = random.nextInt(100000,999999);
		String otpValue = String.valueOf(number);
		userCache.put(user.getEmail(), user);
		otpCache.put(user.getEmail(),otpValue);
		messageData.setTo(user.getEmail());
		messageData.setSubject("verify your email using otp");
		messageData.setSentDate(new Date());
		messageData.setText("your otp : "+ otpValue);
		try {
			mailService.sendMail(messageData);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.ACCEPTED.value())
						.setMessage("Seller Created").setData(userMapper.mapToUserResponse(user)));


	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OTPVerificationRequest otpVerificationRequest) {
		
		
		// TODO Auto-generated method stub
		User user = userCache.getIfPresent(otpVerificationRequest.getEmail());
		String otp = otpCache.getIfPresent(otpVerificationRequest.getEmail());
		String email=user.getEmail();
		if(email==null) {
			throw new IllegalArgumentException("invalid email address");
		}
		int atIndex=email.indexOf("@");
		String name=email.substring(0, atIndex);
		if(otp==null) throw new OtpExpiredException("otp expired");
		if(otp.equals(otpVerificationRequest.getOtp())) 
			user.setUserName(name);
		user.setEmailVerified(true);
		user=userRepository.save(user);
		MessageData messageData = new MessageData();
		
		messageData.setTo(user.getEmail());
		messageData.setSubject("User registration is done");
		messageData.setSentDate(new Date());
		messageData.setText("Your registration is successful for  online shopiping " +
		" username: "+user.getUserName());

	       try {
	            mailService.sendMail(messageData);	           
	          
	        } catch (MessagingException e) {
	            throw new EmailNotFoundException("Failed to send confirmation mail");
	        }
	       return ResponseEntity
                   .status(HttpStatus.OK)
                   .body(new ResponseStructure<UserResponse>()
                           .setStatus(HttpStatus.OK.value())
                           .setMessage("User registration successful")
                           .setData(userMapper.mapToUserResponse(user)));
	       

	}

}


