package com.demo.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.domain.GreetingConfiguration;

@RestController
@RequestMapping("/api/greeting")
class GreetingController {

    public GreetingController(GreetingConfiguration greetingConfig, @Qualifier("demoMessage") String demoMessage) {
        this.greetingConfig = greetingConfig;
        this.demoMessage = demoMessage;
    }
    @GetMapping()
    public ResponseEntity<String> greetFromSetting() {
        return ResponseEntity.ok("Hello " + greetingConfig.getName() + ", " + greetingConfig.getMessage());
    }

    @GetMapping("/frombean")
    public ResponseEntity<String> greetFromBean() {
        return ResponseEntity.ok("Message from bean " + demoMessage);
    }

    /* State */
    private final GreetingConfiguration greetingConfig;
    private final String demoMessage;
}
