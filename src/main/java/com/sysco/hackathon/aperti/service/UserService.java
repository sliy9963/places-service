package com.sysco.hackathon.aperti.service;


import com.sysco.hackathon.aperti.dto.CustomerDTO;
import com.sysco.hackathon.aperti.dto.CustomerResponse;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    private ApiUtils apiUtils;


    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    public List<CustomerDTO> getCustomersForOpCoGiven(String opCoId) {
        try {
            CustomerResponse result = restTemplate.getForObject(apiUtils.getOpCoCustomerUrl(opCoId), CustomerResponse.class);
            if (result != null) {
                return List.of(result.getData());
            };
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching user data: " + e.getMessage());
        }
    }

}
