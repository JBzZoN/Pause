package com.josh.life;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeApplication.class, args);
	}

}
