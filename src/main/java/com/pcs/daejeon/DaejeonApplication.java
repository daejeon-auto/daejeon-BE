package com.pcs.daejeon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DaejeonApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaejeonApplication.class, args);
	}

}
