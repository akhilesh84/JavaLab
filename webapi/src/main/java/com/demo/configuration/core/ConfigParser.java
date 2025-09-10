package com.demo.configuration.core;

import java.io.InputStream;

public interface ConfigParser<T> {
    T parse(InputStream inputStream, Class<T> configClass) throws Exception;
    String getFormat();
}
