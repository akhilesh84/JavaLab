package com.demo.configuration.core;

import java.io.InputStream;
import java.util.Optional;


/**
 * Represent the contract for an abstraction responsible for loading resources from various locations such as
 * classpath, file system, or URL.
 */
public interface ResourceLoader {
    Optional<InputStream> loadResource(String resourcePath);
    boolean canHandle(String resourcePath);
}
