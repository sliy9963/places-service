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

    private SfdcService sfdcService;

    private PlaceService placeService;


    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    @Autowired
    public void setSfdcService(SfdcService sfdcService) {
        this.sfdcService = sfdcService;
    }

    @Autowired
    public void setPlaceService(PlaceService placeService) {
        this.placeService = placeService;
    }

    public List<Object> getCustomersForOpCoGiven(String opCoId) {
        try {
            CustomerResponse result = restTemplate.getForObject(apiUtils.getOpCoCustomerUrl(opCoId), CustomerResponse.class);
            if (result != null) {
                List<String> customerKeys = result.getData().stream().map(customer -> customer.getOpco() + "-" + customer.getCustomerId()).toList();
                List<Object> customerInfo = sfdcService.getCustomerInfo(customerKeys);
                // TODO: merge customer info with place API details

                return customerInfo;
            };
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching user data: " + e.getMessage());
        }
    }

}
