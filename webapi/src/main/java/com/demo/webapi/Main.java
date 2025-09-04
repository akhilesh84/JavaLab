package com.demo.webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

//By default, Spring Boot only scans for components (like @RestController, @ConfigurationProperties) in the same
// package and sub-packages as the main application class. Since com.demo.controllers is not a sub-package of
// com.demo.webapi, Spring Boot won't find our controller. Same with GreetingConfiguration in com.demo.domain.
// To fix this, we can use scanBasePackages to tell Spring Boot to scan the com.demo package and all its sub-packages.

@SpringBootApplication(scanBasePackages = "com.demo")
@ConfigurationPropertiesScan(basePackages = "com.demo")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
