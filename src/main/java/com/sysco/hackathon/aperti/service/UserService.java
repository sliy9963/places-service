package com.sysco.hackathon.aperti.service;


import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.sysco.hackathon.aperti.dto.customer.CustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
                    List<PlaceDetails> customerDataFromGoogle = placeService.getPlaceDetails(query);
                    if (customerDataFromGoogle.size() > 0) {
                        PlaceDetails placeDetails;
                        placeDetails = customerDataFromGoogle.get(0);
                        OpeningHours openingHours = placeDetails.openingHours != null ? placeDetails.openingHours : placeDetails.secondaryOpeningHours;
                        OpeningHours.Period[] periods = openingHours.periods;
                        List<WindowDTO> windows = new ArrayList<>();
                        for (OpeningHours.Period period : periods) {
                            WindowItemDTO googleBusinessHours = generateGoogleWindows(period);
                            WindowDTO window = generateCompleteWindow(period, googleBusinessHours);
                            windows.add(window);
                        }
                        CustomerDetailsDTO customerDetails = generateCustomerInfo(customerInfo, opCoId, windows);
                        customers.add(customerDetails);
                    }
                }
            }
            return customers;
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching user data: " + e.getMessage());
        }
    }

    private WindowItemDTO generateGoogleWindows(OpeningHours.Period period) {
        return WindowItemDTO.builder()
            .from(String.valueOf(period.open.time))
            .to(String.valueOf(period.close.time))
        .build();
    }

    private WindowDTO generateCompleteWindow(OpeningHours.Period period, WindowItemDTO googleBusinessHours) {
        return WindowDTO.builder()
            .day(period.open.day.getName())
            .window(null)
            .googleBusinessHours(googleBusinessHours)
            .exception(apiUtils.generateExceptionLevel())
            .reasonCode(apiUtils.generateReasonCode())
        .build();
    }

    private CustomerDetailsDTO generateCustomerInfo(SfdcCustomerDTO customerInfo, String opCoId, List<WindowDTO> windows) {
        String customerId = customerInfo.getAccount_ID__c() != null ? customerInfo.getAccount_ID__c().split("-")[1] : "N/A";
        return CustomerDetailsDTO.builder()
            .customerId(customerId)
            .opcoId(opCoId)
            .shopName(customerInfo.getName())
            .windows(windows)
        .build();
    }

}
