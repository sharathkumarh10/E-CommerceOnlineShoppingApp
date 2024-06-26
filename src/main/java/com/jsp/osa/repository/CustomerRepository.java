package com.jsp.osa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.osa.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	

}
