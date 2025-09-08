package com.demo.domain.entities.mutualfunds;


import com.fasterxml.jackson.annotation.JsonProperty;

public class MutualFundScheme{
    @JsonProperty("schemeCode")
    public int schemeCode;

    @JsonProperty("schemeName")
    public String schemeName;

    @JsonProperty("isinGrowth")
    public String isinGrowth;

    @JsonProperty("isinDivReinvestment")
    public String isinDivReinvestment;
}

