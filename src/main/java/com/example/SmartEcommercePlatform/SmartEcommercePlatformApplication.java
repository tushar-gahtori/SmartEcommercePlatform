package com.example.SmartEcommercePlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SmartEcommercePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartEcommercePlatformApplication.class, args);
	}

}
