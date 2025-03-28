package com.hiroc.rangero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RangeroApplication {
	public static void main(String[] args) {
		SpringApplication.run(RangeroApplication.class, args);
	}

}
