package com.demo.beans;

import com.demo.services.StudentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoBeans {

    @Bean(name = "demoMessage")
    public String getMessage() {
        return "Hello from DemoBeans!";
    }
}
