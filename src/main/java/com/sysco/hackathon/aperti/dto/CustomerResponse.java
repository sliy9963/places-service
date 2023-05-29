package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerResponse {
    CustomerDTO[] data;
    Object meta;
    Object _links;
    @JsonProperty("has_error")
    boolean hasError;
}
