package com.sysco.hackathon.aperti.service;


import com.google.maps.model.OpeningHours;
import com.sysco.hackathon.aperti.dto.customer.CustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    public List<CustomerDetailsDTO> getCustomersForOpCoGiven(String opCoId) {
        try {
            CustomerResponseDTO result = restTemplate.getForObject(apiUtils.getOpCoCustomerUrl(opCoId), CustomerResponseDTO.class);
            List<CustomerDetailsDTO> customers = new ArrayList<>();
            if (result != null) {
                List<String> customerKeys = result.getData().stream().map(customer -> customer.getOpco() + "-" + customer.getCustomerId()).toList();
                List<SfdcCustomerDTO> customerInfoList = sfdcService.getCustomerInfo(customerKeys);
                for (SfdcCustomerDTO customerInfo : customerInfoList) {
                    String query = customerInfo.getName().toLowerCase();
                    List<OpeningHours> customerOpeningHours = placeService.getPlaceOpeningHours(query);
                    List<WindowDTO> windows = new ArrayList<>();
//                    for (OpeningHours openingHours : customerOpeningHours) {
//                        OpeningHours.Period[] periods = openingHours.periods;
//                        for (OpeningHours.Period period : periods) {
                            WindowDTO window = WindowDTO.builder()
                                    .day(null)
                                    .window(null)
                                    .googleBusinessHours(null)
                                    .build();
                            windows.add(window);
//                        }
//                    }
                    CustomerDetailsDTO customerDetails = CustomerDetailsDTO.builder()
                            .customerId(customerInfo.getAccount_ID__c().split("-")[1])
                            .opcoId(opCoId)
                            .shopName(customerInfo.getName())
                            .windows(windows).build();
                    customers.add(customerDetails);
                }
            }
            return customers;
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching user data: " + e.getMessage());
        }
    }

}
