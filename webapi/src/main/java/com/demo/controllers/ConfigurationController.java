package com.demo.controllers;

import com.demo.configuration.AppConfig;
import com.demo.configuration.ServiceConfig;
import com.demo.configuration.core.FileConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RestController
@RequestMapping("/config")
class ConfigurationController {

    @Autowired
    private FileConfigLoader fileConfigLoader;

    @Autowired
    private AppConfig appConfigFromXml;

    public ConfigurationController() {
    }

    @GetMapping("/services")
    public AppConfig getAllServices() {
        return appConfigFromXml;
    }

    @GetMapping("/service/{serviceName}")
    public ServiceConfig getServiceConfig(@PathVariable("serviceName") String serviceName) {
        return appConfigFromXml.getServiceByName(serviceName);
    }

    @GetMapping("/load/{format}")
    public String loadConfigFromDifferentFormat(@PathVariable("format") String format) {
        String configFile = "appConfig." + format;
        Optional<AppConfig> config = fileConfigLoader.loadConfig(configFile, format, AppConfig.class);

        return config.map(appConfig -> "Successfully loaded " + format.toUpperCase() + " config with " +
                appConfig.getServices().size() + " services").orElseGet(() -> "Failed to load " + format.toUpperCase() + " configuration");
    }

    @GetMapping("/demo-fallback")
    public String demonstrateFallback() {
        String[] fallbackPaths = {
            "https://nonexistent-server.com/config.xml",
            "file:/nonexistent/path/config.xml",
            "appConfig.xml"  // This will succeed
        };

        AppConfig config = fileConfigLoader.loadConfigWithFallback(
            fallbackPaths, "xml", AppConfig.class, new AppConfig()
        );

        return "Fallback demo: Loaded config with " +
               (config.getServices() != null ? config.getServices().size() : 0) + " services";
    }
}
