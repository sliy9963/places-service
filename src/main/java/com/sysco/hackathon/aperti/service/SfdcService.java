package com.sysco.hackathon.aperti.service;


import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcRequestDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcAuthResponseDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static com.sysco.hackathon.aperti.util.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class SfdcService {

    private ApiUtils apiUtils;

    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    @Autowired
    private RestTemplate restTemplate;

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

    public List<SfdcCustomerDTO> getCustomerInfo(List<String> customerKeys) {
        List<List<String>> chunkedCustomerKeys = apiUtils.chunkList(customerKeys, chunkSize);
        SfdcAuthResponseDTO sfdcAuthResponse = getSfdcAuthResponse();
        String queryUrl;
        if (sfdcAuthResponse != null) {
            queryUrl = sfdcAuthResponse.getInstanceUrl() + SFDC_API_URL_SEGMENT;
            SfdcCustomerResponseDTO res = null;
            for (List<String> customerKeysChunk : chunkedCustomerKeys) {
                String query = apiUtils.getQuery(customerKeysChunk);
                res = executeQuery(queryUrl, query, sfdcAuthResponse.getAccessToken());
            }
            if (res != null) return res.getRecords();
        }
        return Collections.emptyList();
    }

    private SfdcAuthResponseDTO getSfdcAuthResponse() {
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
                .postForObject(sfdcAuthUrl, entity, SfdcAuthResponseDTO.class);
    }

    private SfdcCustomerResponseDTO executeQuery(String url, String query, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(APPLICATION_JSON));
        headers.setBearerAuth(token);
        String queryUrl = String.format("%s%s", url, query);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<SfdcCustomerResponseDTO> response = restTemplate.exchange(
                queryUrl, HttpMethod.GET, requestEntity, SfdcCustomerResponseDTO.class);
        return response.getBody();
    }

}
