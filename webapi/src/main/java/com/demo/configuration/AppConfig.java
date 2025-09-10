package com.demo.configuration;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JacksonXmlRootElement(localName = "appConfig")
public class AppConfig {

    @JacksonXmlProperty(localName = "service")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ServiceConfig> services;

    public AppConfig() {}

    public ServiceConfig getServiceByName(String name) {
        if (services == null) return null;
        return services.stream()
                .filter(service -> name.equals(service.getName()))
                .findFirst()
                .orElse(null);
    }
}
