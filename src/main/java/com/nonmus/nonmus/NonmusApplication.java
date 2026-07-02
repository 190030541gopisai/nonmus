package com.nonmus.nonmus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NonmusApplication {

	public static void main(String[] args) {
		SpringApplication.run(NonmusApplication.class, args);
	}

}
