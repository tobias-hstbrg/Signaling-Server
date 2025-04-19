package com.example.signaling_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SignalingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignalingServerApplication.class, args);
	}
}
