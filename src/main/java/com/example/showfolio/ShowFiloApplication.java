package com.example.showfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ShowFiloApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShowFiloApplication.class, args);
    }

}
