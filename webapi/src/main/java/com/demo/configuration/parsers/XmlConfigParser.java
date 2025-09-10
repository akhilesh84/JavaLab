package com.demo.configuration.parsers;

import com.demo.configuration.core.ConfigParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class XmlConfigParser implements ConfigParser<Object> {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public Object parse(InputStream inputStream, Class<Object> configClass) throws Exception {
        return xmlMapper.readValue(inputStream, configClass);
    }

    @Override
    public String getFormat() {
        return "xml";
    }
}
