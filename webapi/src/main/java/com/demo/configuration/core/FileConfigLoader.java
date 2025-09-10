package com.demo.configuration.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileConfigLoader {

    private final List<ResourceLoader> resourceLoaders;
    private final Map<String, ConfigParser<Object>> configParsers;

    @Autowired
    public FileConfigLoader(List<ResourceLoader> resourceLoaders, List<ConfigParser<Object>> configParsers) {
        this.resourceLoaders = resourceLoaders;
        this.configParsers = configParsers.stream()
                .collect(Collectors.toMap(ConfigParser::getFormat, Function.identity()));
    }

    public <T> Optional<T> loadConfig(String resourcePath, String format, Class<T> configClass) {
        // Try each resource loader until one succeeds
        for (ResourceLoader loader : resourceLoaders) {
            if (loader.canHandle(resourcePath)) {
                Optional<InputStream> inputStream = loader.loadResource(resourcePath);
                if (inputStream.isPresent()) {
                    try {
                        ConfigParser<Object> parser = configParsers.get(format.toLowerCase());
                        if (parser != null) {
                            @SuppressWarnings("unchecked")
                            T config = (T) parser.parse(inputStream.get(), (Class<Object>) configClass);
                            return Optional.of(config);
                        }
                    } catch (Exception e) {
                        // Log error and continue to next loader
                    } finally {
                        try {
                            inputStream.get().close();
                        } catch (Exception e) {
                            // Log error
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public <T> T loadConfigWithFallback(String[] resourcePaths, String format, Class<T> configClass, T defaultConfig) {
        for (String path : resourcePaths) {
            Optional<T> config = loadConfig(path, format, configClass);
            if (config.isPresent()) {
                return config.get();
            }
        }
        return defaultConfig;
    }
}
