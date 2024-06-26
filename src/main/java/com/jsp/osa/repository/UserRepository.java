package com.jsp.osa.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

		
	

}
