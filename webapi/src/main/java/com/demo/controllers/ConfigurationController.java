package com.demo.controllers;

import com.demo.configuration.AppConfig;
import com.demo.configuration.AppConfigurationLoader;
import com.demo.configuration.ServiceConfig;
import com.demo.configuration.core.FileConfigLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
* In this controller, I've injected both AppConfigurationLoader and FileConfigLoader just for the same of
* demonstration. In a real application, the configuration will be made available to the cntroller in the form
* of a bean which is loaded once at the application startup.
*
* The more important thing to see here is the usage of a loader object which has the intelligence to load
* configuration from different sources and formats. This is a more flexible approach than hardcoding the
* configuration loading logic in the controller itself.
* */
@RestController
@RequestMapping("/config")
class ConfigurationController {

    private final FileConfigLoader fileConfigLoader;
    private final AppConfigurationLoader appConfigLoader;

    public ConfigurationController(FileConfigLoader fileConfigLoader, AppConfigurationLoader appConfigLoader) {
        this.fileConfigLoader = fileConfigLoader;
        this.appConfigLoader = appConfigLoader;
    }

    // Original endpoint - no parameters
    @GetMapping("/services")
    public AppConfig getAllServices(@RequestParam(value = "format", defaultValue = "xml") String format) {
        String configFile = "appConfig." + format;
        Optional<AppConfig> config;

        return fileConfigLoader.loadConfig(configFile, format, AppConfig.class).orElse(new AppConfig());
    }

    // New endpoint with optional query parameters
    @GetMapping("/services/filtered")
    public List<ServiceConfig> getFilteredServices(
            @RequestParam(value = "version", required = false) String version,
            @RequestParam(value = "timeoutGreaterThan", required = false) Integer timeoutThreshold,
            @RequestParam(value = "nameContains", required = false) String namePattern) {

        AppConfig config = appConfigLoader.appConfigFromXml();
        List<ServiceConfig> services = config.getServices();

        if (services == null) {
            return List.of();
        }

        return services.stream()
                .filter(service -> version == null || version.equals(service.getVersion()))
                .filter(service -> timeoutThreshold == null || service.getTimeout() > timeoutThreshold)
                .filter(service -> namePattern == null || service.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .collect(Collectors.toList());
    }

    @GetMapping("/service/{serviceName}")
    public ServiceConfig getServiceConfig(
            @PathVariable("serviceName") String serviceName,
            @RequestParam(value = "includeMetadata", defaultValue = "false") boolean includeMetadata) {

        ServiceConfig service = appConfigLoader.appConfigFromXml().getServiceByName(serviceName);

        // If includeMetadata is true, you could enhance the response with additional info
        if (includeMetadata && service != null) {
            // For demo purposes, just return the service as-is
            // In real scenario, you might add metadata fields
        }

        return service;
    }

    @GetMapping("/load/{format}")
    public AppConfig loadConfigFromDifferentFormat(
            @PathVariable("format") String format,
            @RequestParam(value = "fallback", defaultValue = "true") boolean useFallback,
            @RequestParam(value = "validateTimeout", required = false) Integer minTimeout) {

        String configFile = "appConfig." + format;
        Optional<AppConfig> config;

        if (useFallback) {
            String[] fallbackPaths = {
                "https://config-server.example.com/" + configFile,
                "file:/etc/myapp/" + configFile,
                configFile  // classpath
            };
            config = Optional.of(fileConfigLoader.loadConfigWithFallback(
                fallbackPaths, format, AppConfig.class, new AppConfig()));
        } else {
            config = fileConfigLoader.loadConfig(configFile, format, AppConfig.class);
        }

        AppConfig result = config.orElse(new AppConfig());

        // Apply validation if minTimeout is specified
        if (minTimeout != null && result.getServices() != null) {
            result.getServices().removeIf(service -> service.getTimeout() < minTimeout);
        }

        return result;
    }

    @GetMapping("/demo-fallback")
    public String demonstrateFallback(
            @RequestParam(value = "format", defaultValue = "xml") String format,
            @RequestParam(value = "includeDetails", defaultValue = "false") boolean includeDetails) {

        String[] fallbackPaths = {
            "https://nonexistent-server.com/config." + format,
            "file:/nonexistent/path/config." + format,
            "appConfig." + format  // This will succeed
        };

        AppConfig config = fileConfigLoader.loadConfigWithFallback(
            fallbackPaths, format, AppConfig.class, new AppConfig()
        );

        String result = "Fallback demo: Loaded " + format.toUpperCase() + " config with " +
               (config.getServices() != null ? config.getServices().size() : 0) + " services";

        if (includeDetails && config.getServices() != null) {
            result += ". Services: " + config.getServices().stream()
                    .map(ServiceConfig::getName)
                    .collect(Collectors.joining(", "));
        }

        return result;
    }

    // New endpoint demonstrating multiple query parameters with different types
    @GetMapping("/search")
    public List<ServiceConfig> searchServices(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "versions", required = false) List<String> versions,
            @RequestParam(value = "minTimeout", required = false, defaultValue = "0") int minTimeout,
            @RequestParam(value = "maxTimeout", required = false, defaultValue = "999999") int maxTimeout,
            @RequestParam(value = "endpointContains", required = false) String endpointPattern) {

        AppConfig config = appConfigLoader.appConfigFromXml();
        List<ServiceConfig> services = config.getServices();

        if (services == null) {
            return List.of();
        }

        return services.stream()
                .filter(service -> name == null || service.getName().equalsIgnoreCase(name))
                .filter(service -> versions == null || versions.contains(service.getVersion()))
                .filter(service -> service.getTimeout() >= minTimeout && service.getTimeout() <= maxTimeout)
                .filter(service -> endpointPattern == null ||
                        service.getEndpoint().toLowerCase().contains(endpointPattern.toLowerCase()))
                .collect(Collectors.toList());
    }
}
