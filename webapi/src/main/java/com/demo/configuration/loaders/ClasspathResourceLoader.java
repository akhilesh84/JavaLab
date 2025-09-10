package com.demo.configuration.loaders;

import com.demo.configuration.core.ResourceLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@Component
public class ClasspathResourceLoader implements ResourceLoader {

    @Override
    public Optional<InputStream> loadResource(String resourcePath) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                return Optional.of(resource.getInputStream());
            }
        } catch (Exception e) {
            // Log error
        }
        return Optional.empty();
    }

    @Override
    public boolean canHandle(String resourcePath) {
        return resourcePath.startsWith("classpath:") ||
               (!resourcePath.startsWith("http") && !resourcePath.startsWith("file:"));
    }
}
