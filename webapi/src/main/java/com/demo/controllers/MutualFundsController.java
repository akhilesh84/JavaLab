package com.demo.controllers;

import com.demo.domain.entities.mutualfunds.FundHistoricData;
import com.demo.domain.entities.mutualfunds.MutualFundScheme;
import com.demo.services.MutualFundsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/mutualfunds")
class MutualFundsController {

    private final MutualFundsService mutualFundsService;

    public MutualFundsController(MutualFundsService mutualFundsService){
        this.mutualFundsService = mutualFundsService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<MutualFundScheme>> getAllschemes() {
        CompletableFuture<List<MutualFundScheme>> gettingFunds = mutualFundsService.getAllSchemes();

        List<MutualFundScheme> result = gettingFunds.join();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/historicdata/{fundId}")
    public ResponseEntity<FundHistoricData> getHistoricData(@PathVariable("fundId") int fundId) {
        if(fundId <= 0) {
            return ResponseEntity.notFound().build();
        }
        CompletableFuture<FundHistoricData> gettingFundHistoricData = mutualFundsService.getFundHistoricData(fundId);

        FundHistoricData result = gettingFundHistoricData.join();
        return ResponseEntity.ok(result);
    }
}
