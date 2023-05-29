package com.sysco.hackathon.aperti.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SfdcAuthResponseDTO {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("instance_url")
    String instanceUrl;
    @JsonProperty("token_type")
    String tokenType;
    @JsonProperty("issued_at")
    String issuedAt;
    String id;
    String signature;
}
