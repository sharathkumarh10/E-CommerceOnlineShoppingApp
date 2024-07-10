package com.jsp.osa.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.jsp.osa.entity.AccessToken;
import com.jsp.osa.entity.Customer;
import com.jsp.osa.entity.RefreshToken;
import com.jsp.osa.entity.Seller;
import com.jsp.osa.entity.User;
import com.jsp.osa.enums.UserRole;
import com.jsp.osa.exception.EmailNotFoundException;
import com.jsp.osa.exception.IncorrectOTPException;
import com.jsp.osa.exception.OtpExpiredException;
import com.jsp.osa.exception.TokenExpiredException;
import com.jsp.osa.exception.UserNotLoggedInException;
import com.jsp.osa.exception.UsernameNotFoundException;
import com.jsp.osa.mapper.UserMapper;
import com.jsp.osa.repository.AccessTokenRepo;
import com.jsp.osa.repository.RefreshTokenRepo;
import com.jsp.osa.repository.UserRepository;
import com.jsp.osa.requestdto.AuthRequest;
import com.jsp.osa.requestdto.OTPVerificationRequest;
import com.jsp.osa.requestdto.UserRequest;
import com.jsp.osa.responsedto.AuthResponse;
import com.jsp.osa.responsedto.UserResponse;
import com.jsp.osa.security.JwtService;
import com.jsp.osa.service.UserService;
import com.jsp.osa.utility.MessageData;
import com.jsp.osa.utility.ResponseStructure;
import com.jsp.osa.utility.SimpleStructure;

import jakarta.mail.MessagingException;

@Service

public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final Cache<String, User> userCache;
	private final Random random;
	private final Cache<String, String> otpCache;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final AccessTokenRepo accessTokenRepository;
	private final RefreshTokenRepo refreshTokenRepository;

	@Value("${application.jwt.access_expiry_seconds}")
	private long accessExpirySeconds;

	@Value("${application.jwt.refresh_expiry_seconds}")
	private long refreshExpirySeconds;

	@Value("${application.cookie.domain}")
	private String domain;

	@Value("${application.cookie.same_site}")
	private String sameSite;

	@Value("${application.cookie.secure}")
	private Boolean secure;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, Cache<String, User> userCache,
			Random random, Cache<String, String> otpCache, MailService mailService,
			AuthenticationManager authenticationManager, JwtService jwtService, AccessTokenRepo accessTokenRepository,
			RefreshTokenRepo refreshTokenRepository) {
		super();
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.userCache = userCache;
		this.random = random;
		this.otpCache = otpCache;
		this.mailService = mailService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.accessTokenRepository = accessTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole userRole) {

		User user = null;
		MessageData messageData = new MessageData();

		switch (userRole) {
		case CUSTOMER -> user = new Customer();
		case SELLER -> user = new Seller();
		}
		if (user != null) {
			user = userMapper.mapToUser(userRequest, user);
			user.setUserRole(userRole);
		}

		int number = random.nextInt(100000, 999999);
		String otpValue = String.valueOf(number);
		userCache.put(user.getEmail(), user);
		otpCache.put(user.getEmail(), otpValue);
		messageData.setTo(user.getEmail());
		messageData.setSubject("verify your email using otp");
		messageData.setSentDate(new Date());
		messageData.setText("your otp : " + otpValue);
		try {
			mailService.sendMail(messageData);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.ACCEPTED.value())
						.setMessage("user registration successful. Please check your email for OTP verifiacation")
						.setData(userMapper.mapToUserResponse(user)));

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OTPVerificationRequest otpVerificationRequest) {

		// TODO Auto-generated method stub

		User user = userCache.getIfPresent(otpVerificationRequest.getEmail());
		String existingotp = otpCache.getIfPresent(otpVerificationRequest.getEmail());
		String requestedotp = otpVerificationRequest.getOtp();
		if (user == null)
			throw new UsernameNotFoundException("User not found");
		else if (existingotp == null && existingotp.equals(requestedotp))
			throw new OtpExpiredException("Otp is expired");
		else if (existingotp.equals(requestedotp)) {

			String email = user.getEmail();
			{
				if (email == null) {
					throw new IllegalArgumentException("invalid email address");
				}
			}
			int atIndex = email.indexOf("@");
			String name = email.substring(0, atIndex);
			user.setUserName(name);
			user.setEmailVerified(true);
			user = userRepository.save(user);
			MessageData messageData = new MessageData();

			messageData.setTo(user.getEmail());
			messageData.setSubject("User registration is done");
			messageData.setSentDate(new Date());
			messageData.setText(
					"Your registration is successful for  online shopiping " + " username: " + user.getUserName());

			try {
				mailService.sendMail(messageData);

			} catch (MessagingException e) {
				throw new EmailNotFoundException("Failed to send confirmation mail");
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseStructure<UserResponse>().setStatus(HttpStatus.OK.value())
							.setMessage("User registration successful").setData(userMapper.mapToUserResponse(user)));

		} else
			throw new IncorrectOTPException("Invalid otp");
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));

		if (authentication.isAuthenticated()) {

			return userRepository.findByUserName(authRequest.getUserName()).map(user -> {
				HttpHeaders headers = new HttpHeaders();
				grantAccessToken(headers, user);
				grantRefreshtoken(headers, user);
				return ResponseEntity.ok().headers(headers)
						.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
								.setMessage("login successfully")
								.setData(AuthResponse.builder()
										.userId(user.getUserId())
										.userName(user.getUserName())
										.roles(user.getUserRole().toString())
										.accessExpiration(accessExpirySeconds)
										.refreshExpiration(refreshExpirySeconds).build()));
			}).orElseThrow(() -> new UsernameNotFoundException(" User name is not found"));

		} else
			throw new BadCredentialsException("Bad Request");
	}

	private void grantAccessToken(HttpHeaders httpHeaders, User user) {

		String accessToken = jwtService.createJwtToken(user.getUserName(), 3600000, user.getUserRole().toString());

		AccessToken access = new AccessToken();
		access.setToken(accessToken);
		access.setExpiration(LocalDateTime.now().plusSeconds(3600));
		access.setBlocked(false);
		access.setUser(user);

		httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", accessToken, accessExpirySeconds));

		accessTokenRepository.save(access);

	}

	private void grantRefreshtoken(HttpHeaders httpHeaders, User user) {

		String refreshToken = jwtService.createJwtToken(user.getUserName(), 1000 * 60 * 60 * 24 * 15,
				user.getUserRole().toString());

		RefreshToken access = new RefreshToken();
		access.setRefreshToken(refreshToken);
		access.setExpiration(LocalDateTime.now().plusSeconds(60 * 60 * 24 * 15));
		access.setBlocked(false);
		access.setUser(user);

		httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", refreshToken, refreshExpirySeconds));

		refreshTokenRepository.save(access);

	}

	private String generateCookie(String name, String value, long maxAge) {

		return ResponseCookie.from(name, value).domain(domain).path("/").maxAge(maxAge).sameSite(sameSite)
				.httpOnly(true).secure(secure).build().toString();

	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshlogin(String refreshToken) {
		// TODO Auto-generated method stub

		Date date = jwtService.extractExpireDate(refreshToken);

		if (date.getTime() < new Date().getTime()) {
			throw new TokenExpiredException("Refresh token was expired, Please make a new SignIn request");
		} else {
			String username = jwtService.extractUserName(refreshToken);
			String userRole = jwtService.extractUserRole(refreshToken);
			User user = userRepository.findByUserName(username).get();

			List<AccessToken> allAT = accessTokenRepository.findAll();
			for (AccessToken at : allAT) {
				if (at.getExpiration().getSecond() < new Date().getTime()) {
					accessTokenRepository.delete(at);
				}
			}

			HttpHeaders httpHeaders = new HttpHeaders();
			grantAccessToken(httpHeaders, user);

			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders)
					.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
							.setMessage("Access Toke renewed")
							.setData(AuthResponse.builder().userId(user.getUserId()).userName(user.getUserName())
									.roles(userRole).accessExpiration(accessExpirySeconds)
									.refreshExpiration(date.getTime() - (LocalDateTime.now().getSecond())).build()));

		}

	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(String refreshToken, String accessToken) {

		if (refreshToken == null || accessToken == null)
			throw new UserNotLoggedInException("Please login first");
		else {
			Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
			Optional<AccessToken> optionalAccessToken = accessTokenRepository.findByToken(accessToken);
			RefreshToken existRefreshToken = optionalRefreshToken.get();
			AccessToken existAccessToken = optionalAccessToken.get();

			existRefreshToken.setBlocked(true);
			existAccessToken.setBlocked(true);
			refreshTokenRepository.save(existRefreshToken);
			accessTokenRepository.save(existAccessToken);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", null, 0));
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", null, 0));

			User user = existRefreshToken.getUser();
			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders)
					.body(new ResponseStructure<AuthResponse>().setStatus(HttpStatus.OK.value())
							.setMessage("User logout done")
							.setData(AuthResponse.builder().userId(user.getUserId()).userName(user.getUserName())
									.roles(user.getUserRole().toString()).accessExpiration(0).refreshExpiration(0)
									.build()));
			// TODO Auto-generated method stub
		}
	}

	@Override
	public ResponseEntity<SimpleStructure> logoutFromOtherDevices(String refreshToken, String accessToken) {
		// TODO Auto-generated method stub
		String userName = jwtService.extractUserName(refreshToken);
		return userRepository.findByUserName(userName).map(user -> {

			accessTokenRepository.findByUserAndIsBlockedAndTokenNot(user, false,accessToken).forEach(at -> {
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});

			refreshTokenRepository.findByUserAndIsBlockedAndRefreshTokenNot(user, false,refreshToken).forEach(rt -> {
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});

		

			return ResponseEntity.status(HttpStatus.OK).body(new SimpleStructure()
					.setStatus(HttpStatus.OK.value()).setMessage("logout from all other devices Successfully"));
		}).orElseThrow(() -> new UsernameNotFoundException("failed to logout from other devices"));

	}

	@Override
	public ResponseEntity<SimpleStructure> logoutFromAllDevices(String accessToken) {
		// TODO Auto-generated method stub

		String userName = jwtService.extractUserName(accessToken);

		return userRepository.findByUserName(userName).map(user -> {

			accessTokenRepository.findByUserAndIsBlocked(user, false).forEach(at -> {
				at.setBlocked(true);
				accessTokenRepository.save(at);
			});

			refreshTokenRepository.findByUserAndIsBlocked(user, false).forEach(rt -> {
				rt.setBlocked(true);
				refreshTokenRepository.save(rt);
			});

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("at", null, 0));
			httpHeaders.add(HttpHeaders.SET_COOKIE, generateCookie("rt", null, 0));

			return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(new SimpleStructure()
					.setStatus(HttpStatus.OK.value()).setMessage("logout from all devices Successfully"));
		}).orElseThrow(() -> new UsernameNotFoundException("failed to logout"));

	}

}
