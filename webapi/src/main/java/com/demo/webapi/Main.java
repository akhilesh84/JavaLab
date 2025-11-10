package com.demo.webapi;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Arrays;
import java.util.logging.Logger;
import org.springframework.kafka.annotation.EnableKafka;

//By default, Spring Boot only scans for components (like @RestController, @ConfigurationProperties) in the same
// package and sub-packages as the main application class. Since com.demo.controllers is not a sub-package of
// com.demo.webapi, Spring Boot won't find our controller. Same with GreetingConfiguration in com.demo.domain.
// To fix this, we can use scanBasePackages to tell Spring Boot to scan the com.demo package and all its sub-packages.

@SpringBootApplication(scanBasePackages = "com.demo")
@ConfigurationPropertiesScan(basePackages = "com.demo")
public class Main {

    public static void main(String[] args) {
        final Logger Logger = java.util.logging.Logger.getLogger(Main.class.getName());
        SpringApplication app = new SpringApplication(Main.class);
        app.setBanner((environment, sourceClass, out) -> {
            out.println("******************************************");
            out.println("*      Welcome to the Web API App       *");
            out.println("******************************************");
        });

        // During the lifecycyle of a spring application, various events are published to signal different stages of
        // the application's startup and shutdown process. We can listen to these events by adding listeners.
        // Here, we add a simple listener that prints the event class name to the console. But more sophisticated
        // logic can be implemented based on the event type.

        // Some events are actually triggered before the ApplicationContext is created, so we cannot register a
        // listener on those as a @Bean. We can register them with the SpringApplication.addListeners(…​) method or
        // the SpringApplicationBuilder.listeners(…​) method.

        //If we want those listeners to be registered automatically, regardless of the way the application is created,
        // we can add a META-INF/spring.factories file to our project and reference your listener(s) by using the
        // ApplicationListener key, as shown in the following example:
        //
        //org.springframework.context.ApplicationListener=com.example.project.MyListener

        // This is also how spring boot autoconfigurations are registered. In principle, the autoconfigurations module
        // subscribes to the ApplicationEnvironmentPreparedEvent event to register additional property sources
        // before the application context is created.

        app.addListeners((event) -> {
            Logger.info("Application event received: " + event.getClass().getName());
        });

        var ctxt = app.run(args);
//        SpringApplication.run(Main.class, args);

        // Get the list of beans registered in application context
        Arrays.stream(ctxt.getBeanDefinitionNames()).forEach(Logger::info);
    }

}
