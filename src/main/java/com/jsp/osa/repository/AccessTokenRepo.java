package com.jsp.osa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer> {


}
