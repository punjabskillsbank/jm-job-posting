package com.jobmatrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.common.entity", "com.jobmatrix.entity"})
public class JmJobPostingApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmJobPostingApplication.class, args);
	}

}
