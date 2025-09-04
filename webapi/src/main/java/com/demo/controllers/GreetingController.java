package com.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.domain.GreetingConfiguration;

@RestController
@RequestMapping("/api/greeting")
class GreetingController {

    public GreetingController(GreetingConfiguration greetingConfig) {
        this.greetingConfig = greetingConfig;
    }
    @GetMapping()
    public ResponseEntity<String> greetFromSetting() {
        return ResponseEntity.ok("Hello " + greetingConfig.getName() + ", " + greetingConfig.getMessage());
    }

    /* State */
    private final GreetingConfiguration greetingConfig;
}
