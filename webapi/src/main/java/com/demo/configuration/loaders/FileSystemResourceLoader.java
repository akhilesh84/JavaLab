package com.demo.configuration.loaders;

import com.demo.configuration.core.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class FileSystemResourceLoader implements ResourceLoader {

    @Override
    public Optional<InputStream> loadResource(String resourcePath) {
        try {
            String path = resourcePath.startsWith("file:") ?
                         resourcePath.substring(5) : resourcePath;

            if (Files.exists(Paths.get(path))) {
                return Optional.of(new FileInputStream(path));
            }
        } catch (Exception e) {
            // Log error
        }
        return Optional.empty();
    }

    @Override
    public boolean canHandle(String resourcePath) {
        return resourcePath.startsWith("file:") || resourcePath.startsWith("/");
    }
}
