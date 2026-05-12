package com.example.tripplanpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TripPlanPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripPlanPracticeApplication.class, args);
	}

}
