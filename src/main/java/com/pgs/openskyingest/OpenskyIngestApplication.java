package com.pgs.openskyingest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
@EnableCaching
public class OpenskyIngestApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenskyIngestApplication.class, args);
	}

}
