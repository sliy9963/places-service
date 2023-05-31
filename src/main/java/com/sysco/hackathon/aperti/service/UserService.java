package com.sysco.hackathon.aperti.service;


import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import com.sysco.hackathon.aperti.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sysco.hackathon.aperti.util.Constants.customerMap;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    private ApiUtils apiUtils;

    private SfdcService sfdcService;

    private PlaceService placeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);


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

    @Autowired
    private ExecutorService taskExecutor;

    public List<CustomerDetailsDTO> getCustomersForOpCoGiven(String opCoId) {
        LOGGER.info("[UserService] Request received: OpCo ID: {}, Request Id: {}", opCoId, UUID.randomUUID());
        try {
            List<CustomerDetailsDTO> customers = new ArrayList<>();
            List<SfdcCustomerDTO> customerInfoList = customerMap.get(opCoId);
            if (customerInfoList != null && customerInfoList.size() > 0) {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (SfdcCustomerDTO customerInfo : customerInfoList) {
                    CompletableFuture<Void> customerFuture = CompletableFuture.runAsync(() -> {
                        LOGGER.info("[UserService] Executing on thread: {}, OpCo: {}, Customer: {}", Thread.currentThread().getName(), opCoId, customerInfo.getName());
                        String query = apiUtils.getPlaceApiQuery(customerInfo);
                        LOGGER.info("[UserService] Place API query: {}, OpCo: {}", query, opCoId);
                        CustomerDetailsDTO customerDetails = generateCustomerWithGoogleWindows(query, customerInfo, opCoId);
                        customers.add(customerDetails);
                    }, taskExecutor);
                    futures.add(customerFuture);
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
            return customers;
        } catch (Exception e) {
            throw new RuntimeException("[UserService] Failed while fetching user data: " + e.getMessage());
        }
    }

    private CustomerDetailsDTO generateCustomerWithGoogleWindows(String query, SfdcCustomerDTO customerInfo, String opCoId) {
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
            List<WindowDTO> windowsUpdated = processWindows(windows);
            customerDetails = generateCustomerInfo(customerInfo, opCoId, windowsUpdated);
        } else {
            customerDetails = generateCustomerInfo(customerInfo, opCoId, getDefaultWindows());
        }
        return customerDetails;
    }

    private List<WindowDTO> processWindows(List<WindowDTO> windows) {
        Map<String, List<WindowDTO>> windowGroups = windows.stream()
                .collect(Collectors.groupingBy(WindowDTO::getDay));
        List<WindowDTO> windowsUpdated = new ArrayList<>();
        for (Map.Entry<String, List<WindowDTO>> e : windowGroups.entrySet()) {
            windowsUpdated.add(e.getValue().get(0));
        }
        return windowsUpdated;
    }

    private WindowItemDTO generateGoogleWindows(OpeningHours.Period period) {
        WindowItemDTO window = WindowItemDTO.builder().build();
        if (period != null) {
            if (period.open != null) {
                window.setFrom(String.valueOf(period.open.time));
            }
            if (period.close != null) {
                window.setTo(String.valueOf(period.close.time));
            }
        }
        return window;
    }

    private WindowDTO generateCompleteWindow(OpeningHours.Period period, WindowItemDTO googleBusinessHours, int index) {
        WindowItemDTO emptyWindow = WindowItemDTO.builder().build();
        WindowDTO window = WindowDTO.builder()
                .window(WindowItemDTO.builder().from("01:00").to("20:00").build())
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
