package com.demo.configuration;

import com.demo.configuration.core.FileConfigLoader;
import org.springframework.stereotype.Service;

@Service
public class AppConfigurationLoader {

    private final FileConfigLoader fileConfigLoader;

    public AppConfigurationLoader(FileConfigLoader fileConfigLoader) {
        this.fileConfigLoader = fileConfigLoader;
    }

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

    public AppConfig appConfigFromYaml() {
        return fileConfigLoader.loadConfig("appConfig.yaml", "yaml", AppConfig.class)
                .orElse(new AppConfig());
    }
}
