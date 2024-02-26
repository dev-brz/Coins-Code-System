package com.cgzt.coinscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CoinsCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinsCodeApplication.class, args);
    }

}
