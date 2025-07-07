package com.jobmatrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EntityScan(basePackages = {"com.common.entity", "com.jobmatrix.entity"})
@Import({com.common.config.CorsConfig.class})
public class JmJobPostingApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmJobPostingApplication.class, args);
	}

}
