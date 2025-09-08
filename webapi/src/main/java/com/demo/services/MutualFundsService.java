package com.demo.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.demo.domain.entities.mutualfunds.FundHistoricData;
import com.demo.domain.entities.mutualfunds.MutualFundScheme;

@Service
@Scope("singleton")
public class MutualFundsService {
    private final HttpClient _httpClient;
    private final ObjectMapper _mapper;
    static final String c_baseUrl = "https://api.mfapi.in/mf";

    public MutualFundsService(){
        _mapper = new ObjectMapper();
        _mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        _httpClient = HttpClient.newHttpClient();
    }

    public CompletableFuture<List<MutualFundScheme>> getAllSchemes() {

    HttpRequest request = HttpRequest
            .newBuilder()
            .uri(URI.create(c_baseUrl))
            .build();

    return _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Failed to fetch data: " + response.statusCode());
                }
                return response;
            })
            .thenApply(HttpResponse::body)
            .thenApply(jsonBody -> {
                try {
                    return _mapper.readValue(jsonBody,
                        _mapper.getTypeFactory().constructCollectionType(List.class, MutualFundScheme.class));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
}

    public CompletableFuture<FundHistoricData> getFundHistoricData(int fundId){

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(c_baseUrl + "/" + fundId))
                .build();

        return _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Failed to fetch data: " + response.statusCode());
                    }
                    return response;
                })
                .thenApply(HttpResponse::body)
                .thenApply(jsonBody -> {
                    try {
                        return _mapper.readValue(jsonBody, FundHistoricData.class);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
