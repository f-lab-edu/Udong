package com.hyun.udong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class UdongApplication {

    public static void main(String[] args) {
        SpringApplication.run(UdongApplication.class, args);
    }

}
