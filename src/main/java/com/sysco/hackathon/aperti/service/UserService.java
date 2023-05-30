package com.sysco.hackathon.aperti.service;


import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.sysco.hackathon.aperti.dto.customer.CustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import com.sysco.hackathon.aperti.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

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

    // TODO: change days logic based on schedule data
    // TODO: change query
    // TODO: create window object based on schedule data
    // TODO: change opening hours propagation based on scheduled data
    public List<CustomerDetailsDTO> getCustomersForOpCoGiven(String opCoId) {
        try {
            CustomerResponseDTO customerServiceResponse = restTemplate.getForObject(apiUtils.getOpCoCustomerUrl(opCoId), CustomerResponseDTO.class);
            List<CustomerDetailsDTO> customers = new ArrayList<>();
            if (customerServiceResponse != null) {
                List<String> customerKeys = apiUtils.getCustomerKeys(customerServiceResponse);
                List<SfdcCustomerDTO> customerInfoList = sfdcService.getCustomerInfo(customerKeys);
                for (SfdcCustomerDTO customerInfo : customerInfoList) {
                    String query = customerInfo.getName().toLowerCase() + " " + customerInfo.getShippingStreet();
                    CustomerDetailsDTO customerDetails;
                    List<PlaceDetails> customerDataFromGoogle = placeService.getPlaceDetails(query);
                    if (customerDataFromGoogle.size() > 0) {
                        PlaceDetails placeDetails = customerDataFromGoogle.get(0);
                        OpeningHours openingHours = placeDetails.openingHours != null ? placeDetails.openingHours : placeDetails.secondaryOpeningHours;
                        List<WindowDTO> windows = new ArrayList<>();
                        if (openingHours != null) {
                            OpeningHours.Period[] periods = openingHours.periods;
                            for (OpeningHours.Period period : periods) {
                                WindowItemDTO googleBusinessHours = generateGoogleWindows(period);
                                WindowDTO window = generateCompleteWindow(period, googleBusinessHours, 0);
                                windows.add(window);
                            }
                        } else {
                            windows = getDefaultWindows();
                        }
                        windows.sort(Comparator.comparingInt((WindowDTO w) -> Integer.parseInt(w.getDay())));
                        customerDetails = generateCustomerInfo(customerInfo, opCoId, windows);
                    } else {
                        customerDetails = generateCustomerInfo(customerInfo, opCoId, getDefaultWindows());
                    }
                    customers.add(customerDetails);
                }
            }
            return customers;
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching user data: " + e.getMessage());
        }
    }

    private WindowItemDTO generateGoogleWindows(OpeningHours.Period period) {
        WindowItemDTO window = WindowItemDTO.builder().build();
        if (period == null) {
            window.setFrom(null);
            window.setFrom(null);
        } else {
            if (period.open != null) {
                window.setFrom(String.valueOf(period.open.time));
            } else {
                window.setFrom(null);
            }
            if (period.close != null) {
                window.setTo(String.valueOf(period.close.time));
            } else {
                window.setTo(null);
            }
        }
        return window;
    }

    private WindowDTO generateCompleteWindow(OpeningHours.Period period, WindowItemDTO googleBusinessHours, int index) {
        WindowItemDTO emptyWindow = WindowItemDTO.builder().build();
        WindowDTO window = WindowDTO.builder()
                .window(null)
                .exception(apiUtils.generateExceptionLevel())
                .reasonCode(apiUtils.generateReasonCode()).build();
        if (googleBusinessHours == null && period == null) {
            window.setGoogleBusinessHours(emptyWindow);
            window.setDay(String.valueOf(index));
        } else {
            window.setGoogleBusinessHours(googleBusinessHours);
            window.setDay(Constants.DayNumberOfWeek.valueOf(period.open.day.getName()).getValue());
        }
        return window;
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

    private List<WindowDTO> getDefaultWindows() {
        List<WindowDTO> windows = new ArrayList<>();
        IntStream.range(0, 7).forEach(i -> {
            WindowDTO window = generateCompleteWindow(null, null, i);
            windows.add(window);
        });
        return windows;
    }

}
