package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomerResponse {
    List<CustomerDTO> data;
    Object meta;
    Object _links;
    @JsonProperty("has_error")
    boolean hasError;
}
