package com.jsp.osa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer> {

	boolean existsByTokenAndIsBlocked(String token, boolean b);

	public RefreshToken findByToken(String token);

}
