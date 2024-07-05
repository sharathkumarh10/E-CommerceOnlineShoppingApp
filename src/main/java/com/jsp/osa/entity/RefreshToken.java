package com.jsp.osa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity

public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int refreshTokenId;
	private String refreshToken;
	private LocalDateTime expiration;
	private boolean isBlocked;
	
	@ManyToOne
	private User user;

}
