package com.demo.domain.entities.mutualfunds;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class FundMeta{
    @JsonProperty("fund_house")
    public String fund_house;

    @JsonProperty("scheme_type")
    public String scheme_type;

    @JsonProperty("scheme_category")
    public String scheme_category;

    @JsonProperty("scheme_code")
    public int scheme_code;

    @JsonProperty("scheme_name")
    public String scheme_name;

    @JsonProperty("isin_growth")
    public String isin_growth;
//    public Object isin_div_reinvestment;
}
