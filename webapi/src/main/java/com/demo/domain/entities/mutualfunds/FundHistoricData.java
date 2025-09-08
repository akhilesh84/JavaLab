package com.demo.domain.entities.mutualfunds;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Getter
@Setter
public class FundHistoricData{
    @JsonProperty("meta")
    public FundMeta meta;

    @JsonProperty("data")
    public List<NavRecord> historicData;

    @JsonProperty("status")
    public String status;
}
