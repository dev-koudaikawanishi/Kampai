package com.kampai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kampai", "com.kampai.controller", "com.kampai.repository", "com.kampai.entity"})
public class KampaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KampaiApplication.class, args);
    }

}
