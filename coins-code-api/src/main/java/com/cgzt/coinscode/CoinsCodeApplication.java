package com.cgzt.coinscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoinsCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoinsCodeApplication.class, args);
    }
}
