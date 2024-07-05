package com.jsp.osa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.AccessToken;
import com.jsp.osa.entity.RefreshToken;
import com.jsp.osa.entity.User;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer> {

	Optional<AccessToken> findByToken(String accessToken);

	List<AccessToken> findByUserAndIsBlocked(User user, boolean b);



}
