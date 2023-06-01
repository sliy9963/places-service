package com.sysco.hackathon.aperti.service;


import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.sysco.hackathon.aperti.dao.CustomerDetailsDAO;
import com.sysco.hackathon.aperti.dto.request.WindowUpdateDTO;
import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import com.sysco.hackathon.aperti.dto.response.WindowUpdateResponse;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.repository.impl.PlaceRepositoryImpl;
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

import static com.sysco.hackathon.aperti.util.Constants.*;

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

    @Autowired
    private PlaceRepositoryImpl placeRepository;

    private static final Random RANDOM = new Random();

    public List<CustomerDetailsDTO> getCustomersForOpCoGiven(String opCoId) {
        LOGGER.info("[UserService] Request received to fetch customer data for OpCo ID: {}", opCoId);
        try {
            List<CustomerDetailsDTO> customers = new ArrayList<>();
            List<SfdcCustomerDTO> customerInfoList = customerMap.get(opCoId);
            if (customerInfoList != null && customerInfoList.size() > 0) {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (SfdcCustomerDTO customerInfo : customerInfoList) {
                    CompletableFuture<Void> customerFuture = CompletableFuture.runAsync(() -> {
                        LOGGER.info("[UserService] Executing on thread: {}, OpCo: {}, Customer: {}", Thread.currentThread().getName(), opCoId, customerInfo.getName());
                        String query = apiUtils.getPlaceApiQuery(customerInfo).trim();
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

    private PlaceDetails getPlaceDetails(String key, String query) {
        PlaceDetails placeDetails = null;
        if (placesMap.containsKey(key)) {
            placeDetails = placesMap.get(key);
            LOGGER.info("Fetching data from cache");
        } else {
            List<PlaceDetails> customerDataFromGoogle = placeService.getPlaceDetails(query);
            if (customerDataFromGoogle.size() > 0) {
                placeDetails = customerDataFromGoogle.get(0);
                placesMap.put(key, placeDetails);
            }
            LOGGER.info("Fetching data from API");
        }
        System.out.println(placesMap);
        return placeDetails;
    }

    private Map<String, WindowDTO> generateScheduleWindowMap(CustomerDetailsDAO customerDetailsDAO) {
        Map<String, WindowDTO> scheduleMap = new HashMap<>();
        // Iterate for seven days
        for (int i=0; i<7; i++) {
            // Should add a delivery to the day
            boolean isDeliveryExistForDay = RANDOM.nextBoolean();
            if (isDeliveryExistForDay) {
                int num = RANDOM.ints(0, 20).findFirst().orElse(0);
                WindowDTO window = WindowDTO.builder()
                        .day(String.valueOf(i))
                        .window(windowsList.get(num))
                        .googleBusinessHours(null)
                        .reasonCode(apiUtils.getDefaultReasonCode()).build();
                updateDataFromDatabase(window, customerDetailsDAO);
                scheduleMap.put(String.valueOf(i), window);
            }
        }
        return scheduleMap;
    }

    private CustomerDetailsDAO fetchCustomerDetailDAO(String opCoId, SfdcCustomerDTO customerInfo) {
        String customerId = customerInfo.getAccount_ID__c() != null ?
                customerInfo.getAccount_ID__c().split("-")[1] : "N/A";
        String key = apiUtils.keyGenerator(opCoId, customerId);
        Optional<CustomerDetailsDAO> customerDetailsDAOOptional = placeRepository.findById(key);
        return customerDetailsDAOOptional.orElse(null);
    }

    private CustomerDetailsDTO generateCustomerWithGoogleWindows(String query, SfdcCustomerDTO customerInfo, String opCoId) {
        CustomerDetailsDAO customerDetailsDAO = fetchCustomerDetailDAO(opCoId, customerInfo);
        Map<String, WindowDTO> scheduledList = generateScheduleWindowMap(customerDetailsDAO);
        CustomerDetailsDTO customerDetails = generateCustomerInfo(customerInfo, opCoId,
                scheduledList.values().stream().toList());
        PlaceDetails placeDetails = getPlaceDetails(customerInfo.getAccount_ID__c(), query);
        if (placeDetails != null) {
            LOGGER.info("[UserService] Customer Found ===> Query: {} | Name: {} | Address: {}",
                    query, placeDetails.name, placeDetails.formattedAddress);
            OpeningHours openingHours = placeDetails.openingHours != null ? placeDetails.openingHours : placeDetails.secondaryOpeningHours;
            List<WindowDTO> windows;
            if (openingHours != null) {
                OpeningHours.Period[] periods = openingHours.periods;
                for (OpeningHours.Period period : periods) {
                    WindowItemDTO googleBusinessHours = generateGoogleWindows(period);
                    String dayIdentifier = Constants.DayNumberOfWeek.valueOf(period.open.day.getName()).getValue();
                    if (scheduledList.get(dayIdentifier) == null) {
                        WindowDTO window = generateCompleteWindow(period, googleBusinessHours, 0);
                        updateDataFromDatabase(window, customerDetailsDAO);
                        scheduledList.put(dayIdentifier, window);
                    } else {
                        WindowDTO window = scheduledList.get(dayIdentifier);
                        if (window.getGoogleBusinessHours() == null) {
                            List<WindowItemDTO> businessHours = new ArrayList<>();
                            businessHours.add(googleBusinessHours);
                            window.setGoogleBusinessHours(businessHours);
                        } else {
                            window.getGoogleBusinessHours().add(googleBusinessHours);
                        }
                        updateDataFromDatabase(window, customerDetailsDAO);
                        window.setException(apiUtils.generateExceptionCode(
                                window.getWindow(), window.getGoogleBusinessHours()));
                        window.setSuggestedWindow(apiUtils.getSuggestedWindow());
                    }
                }
            }
            windows = new ArrayList<>(scheduledList.values());
            windows.sort(Comparator.comparingInt((WindowDTO w) -> Integer.parseInt(w.getDay())));
            List<WindowDTO> windowsUpdated = processWindows(windows);
            customerDetails.setWindows(windowsUpdated);
        }
        String key = apiUtils.keyGenerator(customerDetails.getOpcoId(), customerDetails.getCustomerId());
        placeRepository.saveCustomerPlaceDetails(customerDetails, key);
        return customerDetails;
    }

    private void updateDataFromDatabase(WindowDTO window, CustomerDetailsDAO customerDetailsDAO) {
        String day = window.getDay();
        if (customerDetailsDAO != null && customerDetailsDAO.getWindows() != null &&
                customerDetailsDAO.getWindows().size() > 0) {
            Optional<WindowDTO> optional = customerDetailsDAO.getWindows().stream()
                    .filter(windowDTO -> windowDTO.getDay().equalsIgnoreCase(day))
                    .findFirst();
            if (optional.isPresent()) {
                WindowDTO windowDTO = optional.get();
                window.setReasonCode(apiUtils.getDefaultReasonCode());
                if (windowDTO.getReasonCode() != null) {
                    window.setReasonCode(windowDTO.getReasonCode());
                }
            }
        }
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
        List<WindowItemDTO> googleBusinessHourList = new ArrayList<>();
        googleBusinessHourList.add(googleBusinessHours != null ?
                googleBusinessHours : WindowItemDTO.builder().build());
        WindowDTO window = WindowDTO.builder()
                .window(WindowItemDTO.builder().build())
                .googleBusinessHours(googleBusinessHourList).build();
        if (googleBusinessHours == null && period == null) {
            window.setDay(String.valueOf(index));
        } else {
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

    public WindowUpdateResponse upsertWindowActions(WindowUpdateDTO windowUpdate) {
        String key = apiUtils.keyGenerator(windowUpdate.getOpcoId(), windowUpdate.getCustomerId());
        Optional<CustomerDetailsDAO> recordFound = placeRepository.findById(key);
        if (recordFound.isPresent()) {
            CustomerDetailsDAO record = recordFound.get();
            List<WindowDTO> windows = record.getWindows();
            WindowDTO existingWindowRecord = windows.stream().filter(x -> x.getDay().equals(windowUpdate.getDay())).findFirst().orElse(null);
            if (existingWindowRecord != null) {
                existingWindowRecord.setReasonCode(windowUpdate.getReasonCode());
                existingWindowRecord.setSuggestedWindow(windowUpdate.getSuggestedWindow());
                record.setWindows(windows);
                placeRepository.save(record);
                return WindowUpdateResponse.builder().message("Data updated")
                        .customerId(windowUpdate.getCustomerId()).opcoId(windowUpdate.getOpcoId()).build();
            }
        }
        return WindowUpdateResponse.builder().message("Failed to update").build();
    }

}
