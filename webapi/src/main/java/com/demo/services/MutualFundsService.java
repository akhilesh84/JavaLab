package com.demo.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.demo.domain.entities.mutualfunds.FundHistoricData;
import com.demo.domain.entities.mutualfunds.MutualFundScheme;

@Service
@Scope("singleton")
public class MutualFundsService {
    private final RestTemplate restTemplate;
    private static final String c_BaseUrl = "https://api.mfapi.in/mf";

    public MutualFundsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CompletableFuture<List<MutualFundScheme>> getAllSchemes() {
        return CompletableFuture.supplyAsync(() ->
                restTemplate.exchange(
                        c_BaseUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<@NotNull List<MutualFundScheme>>() {}
                ).getBody()
        );
    }

    public CompletableFuture<FundHistoricData> getFundHistoricData(int fundId) {
        return CompletableFuture.supplyAsync(() ->
                restTemplate.getForObject(
                        c_BaseUrl + "/" + fundId,
                        FundHistoricData.class
                )
        );
    }
}

