package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SfdcRequestDTO {

    /*
    *   const body = {
    client_id: SFDC_CLIENT_ID,
    client_secret: SFDC_CLIENT_SECRET,
    response_type: 'code',
    redirect_uri: SFDC_REDIRECT_URI,
    grant_type: 'password',
    username: SFDC_USERNAME,
    password: SFDC_PASSWORD
  };
    * */
    @JsonProperty("client_id")
    String clientId;
    @JsonProperty("client_secret")
    String clientSecret;
    @JsonProperty("response_type")
    @Builder.Default
    String responseType = "code";
    @Builder.Default
    @JsonProperty("redirect_uri")
    String redirectUri = "Localhost";
    @Builder.Default
    @JsonProperty("grant_type")
    String grantType = "password";
    @Builder.Default
    String username = "cx-salesforce@sysco.com.staging";
    String password;
}
