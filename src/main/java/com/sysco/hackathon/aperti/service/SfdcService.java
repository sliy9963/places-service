package com.sysco.hackathon.aperti.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.hackathon.aperti.dto.CustomerResponse;
import com.sysco.hackathon.aperti.dto.SfdcRequestDTO;
import com.sysco.hackathon.aperti.dto.SfdcResponseDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class SfdcService {

    private ApiUtils apiUtils;

    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    @Value("${application.customer.records.chunk.size}")
    private Integer chunkSize;

    @Value("${application.sfdc.client.id}")
    private String clientId;

    @Value("${application.sfdc.client.secret}")
    private String clientSecret;

    @Value("${application.sfdc.password}")
    private String password;

    @Value("${application.sfdc.auth.url}")
    private String sfdcAuthUrl;

    @Value("${application.sfdc.query.url}")
    private String sfdcQueryUrl;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_DATA_QUERY = "SELECT+Account_ID__c,Name,ShippingStreet,ShippingCity,ShippingState,ShippingPostalCode,Location__c+FROM+Account+WHERE+Account_ID__c=";

    public List<Object> getCustomerInfo(List<String> customerKeys) {
        List<List<String>> chunkedCustomerKeys = apiUtils.chunkList(customerKeys, chunkSize);
        SfdcResponseDTO sfdcAuthResponse = getSfdcAuthResponse();
        // TODO: perform query to SFDC use chunkedCustomerKeys + sfdcQueryUrl + auth
        return Collections.emptyList();
    }

    public SfdcResponseDTO getSfdcAuthResponse() {
        SfdcRequestDTO sfdcAuth = SfdcRequestDTO.builder()
                .clientId(clientId).clientSecret(clientSecret).password(password).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(APPLICATION_JSON));
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("response_type", sfdcAuth.getResponseType());
        requestBody.add("redirect_uri", sfdcAuth.getRedirectUri());
        requestBody.add("grant_type", sfdcAuth.getGrantType());
        requestBody.add("username", sfdcAuth.getUsername());
        requestBody.add("password", password);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
        SfdcResponseDTO response = restTemplate
                .postForObject(sfdcAuthUrl, entity, SfdcResponseDTO.class);
        return response;
    }

}
