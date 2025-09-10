package com.demo.configuration.loaders;

import com.demo.configuration.core.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Component
public class HttpResourceLoader implements ResourceLoader {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Optional<InputStream> loadResource(String resourcePath) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(resourcePath))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                return Optional.of(response.body());
            }
        } catch (Exception e) {
            // Log error
        }
        return Optional.empty();
    }

    @Override
    public boolean canHandle(String resourcePath) {
        return resourcePath.startsWith("http://") || resourcePath.startsWith("https://");
    }
}
