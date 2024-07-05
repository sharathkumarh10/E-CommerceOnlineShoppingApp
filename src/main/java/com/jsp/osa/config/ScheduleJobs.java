package com.jsp.osa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleJobs {
	
//	@Scheduled(fixedDelay = 5000L)
	public void test() {
		
		System.out.println("hiii");
		
	}

}
