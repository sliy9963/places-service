package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDTO {
    String id;
    String opco;
    @JsonProperty("customer_id")
    String customerId;
}
