package com.jsp.osa.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);


	Optional<User> findByUserName(String userName);

		
	

}
