package com.demo.configuration;

import com.demo.configuration.core.FileConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigurationLoader {

    @Autowired
    private FileConfigLoader fileConfigLoader;

    @Bean
    public AppConfig appConfigFromXml() {
        String[] fallbackPaths = {
            "https://config-server.example.com/appConfig.xml",
            "file:/etc/myapp/appConfig.xml",
            "appConfig.xml"  // classpath
        };

        return fileConfigLoader.loadConfigWithFallback(
            fallbackPaths, "xml", AppConfig.class, new AppConfig()
        );
    }

    @Bean
    public AppConfig appConfigFromJson() {
        String[] fallbackPaths = {
            "https://config-server.example.com/appConfig.json",
            "file:/etc/myapp/appConfig.json",
            "appConfig.json"
        };

        return fileConfigLoader.loadConfigWithFallback(
            fallbackPaths, "json", AppConfig.class, new AppConfig()
        );
    }

    @Bean
    public AppConfig appConfigFromYaml() {
        return fileConfigLoader.loadConfig("appConfig.yaml", "yaml", AppConfig.class)
                .orElse(new AppConfig());
    }
}
