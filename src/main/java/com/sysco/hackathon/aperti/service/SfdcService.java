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
import java.util.stream.Collectors;

import static com.sysco.hackathon.aperti.util.Constants.*;
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

    @Value("${application.sfdc.query.url.suffix}")
    private String sfdcQueryUrlSuffix;

    @Autowired
    private RestTemplate restTemplate;

    public List<Object> getCustomerInfo(List<String> customerKeys) {
        List<List<String>> chunkedCustomerKeys = apiUtils.chunkList(customerKeys, chunkSize);
//        SfdcResponseDTO sfdcAuthResponse = getSfdcAuthResponse();
        String queryUrl = "";
//        if (sfdcAuthResponse != null) {
            queryUrl = "https://sysco--staging.sandbox.my.salesforce.com" + sfdcQueryUrlSuffix + "?q=";
//        }
        StringBuilder sfdcQuery = new StringBuilder(USER_DATA_QUERY_FORMAT);
        for (List<String> customerKeysChunk : chunkedCustomerKeys) {
            // create query
            String queryPerChunk = customerKeysChunk.stream()
                    .map(cKey -> sfdcQuery.append("'").append(cKey).append("'").append(USER_DATA_QUERY_JOIN)).collect(Collectors.joining());
            System.out.println("QUERY PER CHUNK: " + queryPerChunk);
            // create one request per chunk
        }
        // TODO: perform query to SFDC use chunkedCustomerKeys + instanceUrl + auth
        return Collections.emptyList();
    }

    private SfdcResponseDTO getSfdcAuthResponse() {
        SfdcRequestDTO sfdcAuth = SfdcRequestDTO.builder()
                .clientId(clientId).clientSecret(clientSecret).password(password).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(APPLICATION_JSON));
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(CLIENT_ID, clientId);
        requestBody.add(CLIENT_SECRET, clientSecret);
        requestBody.add(RESPONSE_TYPE, sfdcAuth.getResponseType());
        requestBody.add(REDIRECT_URI, sfdcAuth.getRedirectUri());
        requestBody.add(GRANT_TYPE, sfdcAuth.getGrantType());
        requestBody.add(USERNAME, sfdcAuth.getUsername());
        requestBody.add(PASSWORD, password);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate
                .postForObject(sfdcAuthUrl, entity, SfdcResponseDTO.class);
    }

    private Object executeQuery(String query) {
        // TODO: implement query to sfdc with rest template
        return null;
    }

}
