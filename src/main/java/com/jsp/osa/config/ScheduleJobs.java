package com.jsp.osa.config;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.jsp.osa.repository.AccessTokenRepo;
import com.jsp.osa.repository.RefreshTokenRepo;

import lombok.AllArgsConstructor;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class ScheduleJobs {

	private AccessTokenRepo accessTokenRepo;
	private RefreshTokenRepo refreshTokenRepo;

	@Scheduled(fixedDelay = 60 * 60 * 1000l)
	public void deleteAllExpiredAccessTokens() {
		accessTokenRepo.findAllByExpirationBefore(LocalDateTime.now()).forEach(accessTokenRepo::delete);
	}

	@Scheduled(fixedDelay = 60 * 60 * 1000l)
	public void deleteAllExpiredRefreshTokens() {
		refreshTokenRepo.findAllByExpirationBefore(LocalDateTime.now()).forEach(refreshTokenRepo::delete);
	}

}
