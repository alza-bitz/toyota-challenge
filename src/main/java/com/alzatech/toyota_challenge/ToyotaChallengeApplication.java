package com.alzatech.toyota_challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.Resource;

@SpringBootApplication
public class ToyotaChallengeApplication {

	@Resource
	private ToyotaChallengeProcess process;

	public static void main(String[] args) {
		SpringApplication.run(ToyotaChallengeApplication.class, args);
	}

}
