package com.sysco.hackathon.aperti.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomerResponseDTO {
    List<CustomerDTO> data;
    Object meta;
    Object _links;
    @JsonProperty("has_error")
    boolean hasError;
}
