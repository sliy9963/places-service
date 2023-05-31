package com.sysco.hackathon.aperti.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.sysco.hackathon.aperti.dto.OpCoDetailsDTO;
import com.sysco.hackathon.aperti.dto.schedule.ScheduledDeliveryDTO;
import com.sysco.hackathon.aperti.dto.customer.CustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.sysco.hackathon.aperti.util.Constants.*;

@Component
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ApiUtils {


    @Value("${application.google.api.key}")
    private String apiKey;

    @Value("${application.customer.service.url}")
    private String customerServiceUrl;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public GeoApiContext getContext() {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public String getEncodedText(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    public String getOpCoCustomerUrl(String opCoId) {
        return String.format("%s/%s/%s/%s", customerServiceUrl, "opcos", opCoId, "customers?page=0&size=50");
    }

    public <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> parts = new ArrayList<>();
        int n = list.size();
        for (int i = 0; i < n; i += chunkSize) {
            parts.add(new ArrayList<>(list.subList(i, Math.min(n, i + chunkSize))));
        }
        return parts;
    }

    public String getQuery(List<String> customerKeysChunk) {
        StringBuilder sfdcQuery = new StringBuilder(USER_DATA_QUERY_FORMAT);
        StringBuilder q = new StringBuilder();
        for (int i = 0; i < customerKeysChunk.size(); i++) {
            if (i == customerKeysChunk.size() - 1) {
                q.append("'").append(customerKeysChunk.get(i)).append("'");
            } else {
                q.append("'").append(customerKeysChunk.get(i)).append("'").append(USER_DATA_QUERY_JOIN);
            }
        }
        sfdcQuery.append(q);
        return sfdcQuery.toString();
    }

    public String generateExceptionLevel() {
        return exceptionsList.get(getRandomNumberFromList(exceptionsList));
    }

    public String generateReasonCode() {
        return reasonCodesList.get(getRandomNumberFromList(reasonCodesList));
    }

    private Integer getRandomNumberFromList(List<String> list) {
        Random random = new Random();
        return random.ints(0, list.size())
                .findFirst()
                .orElse(0);
    }

    public List<String> getCustomerKeys(CustomerResponseDTO result) {
        return result.getData().stream().map(customer -> customer.getOpco() + "-" + customer.getCustomerId()).toList();
    }

    public String getPlaceApiQuery(SfdcCustomerDTO customerInfo) {
        return customerInfo.getName().toLowerCase() + " " + customerInfo.getShippingStreet().toLowerCase();
    }

    public Map<String, OpCoDetailsDTO> readOpCoDataFile() {
        Map<String, OpCoDetailsDTO> map;
        try {
            Resource resource = new ClassPathResource("mockOpcoDetails.json");
            map = OBJECT_MAPPER.readValue(resource.getInputStream(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read opco JSON: " + e);
        }
        return map;
    }

    public List<SfdcCustomerDTO> readCustomerFile(String fileName) {
        List<SfdcCustomerDTO> data;
        try {
            Resource resource = new ClassPathResource(fileName);
            data = OBJECT_MAPPER.readValue(resource.getInputStream(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read opco JSON: " + e);
        }
        return data;
    }

    public Map<String, List<ScheduledDeliveryDTO>> readScheduleFile(String fileName) {
        Map<String, List<ScheduledDeliveryDTO>> data;
        try {
            Resource resource = new ClassPathResource(fileName);
            data = OBJECT_MAPPER.readValue(resource.getInputStream(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to read opco JSON: " + e);
        }
        return data;
    }


}
