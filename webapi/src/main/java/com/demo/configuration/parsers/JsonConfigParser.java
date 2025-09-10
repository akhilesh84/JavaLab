package com.demo.configuration.parsers;

import com.demo.configuration.core.ConfigParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class JsonConfigParser implements ConfigParser<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object parse(InputStream inputStream, Class<Object> configClass) throws Exception {
        return objectMapper.readValue(inputStream, configClass);
    }

    @Override
    public String getFormat() {
        return "json";
    }
}
