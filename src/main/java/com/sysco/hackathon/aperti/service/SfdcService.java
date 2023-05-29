package com.sysco.hackathon.aperti.service;


import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SfdcService {

    private ApiUtils apiUtils;

    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }


    @Value("${application.customer.records.chunk.size}")
    private Integer chunkSize;

    private static final String USER_DATA_QUERY = "SELECT+Account_ID__c,Name,ShippingStreet,ShippingCity,ShippingState,ShippingPostalCode,Location__c+FROM+Account+WHERE+Account_ID__c=";

    public List<Object> getCustomerInfo(List<String> customerKeys) {
        List<List<String>> chunkedCustomerKeys = apiUtils.chunkList(customerKeys, chunkSize);
        System.out.println(chunkedCustomerKeys);
        // TODO: perform query to SFDC
        // TODO: create SFDC auth layer with token
        return Collections.emptyList();
    }
}
