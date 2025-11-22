package com.jobmatrix;

import com.common.config.AwsConfig;
import com.common.config.CorsConfig;
import com.common.config.S3Config;
import com.common.util.S3FileUtil;
import com.common.util.S3PresignedURLUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EntityScan(basePackages = {"com.common.entity", "com.jobmatrix.entity"})
@Import({AwsConfig.class,CorsConfig.class, S3Config.class, S3PresignedURLUtil.class, S3FileUtil.class})
public class JmJobPostingApplication {
	public static void main(String[] args) {
		SpringApplication.run(JmJobPostingApplication.class, args);
	}

}
