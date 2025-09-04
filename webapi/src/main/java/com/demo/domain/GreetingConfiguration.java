package com.demo.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "greeting")
public class GreetingConfiguration {
    private String message;
    private String name;

}
