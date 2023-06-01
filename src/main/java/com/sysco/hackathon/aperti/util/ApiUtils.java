package com.sysco.hackathon.aperti.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.sysco.hackathon.aperti.dto.OpCoDetailsDTO;
import com.sysco.hackathon.aperti.dto.customer.CustomerResponseDTO;
import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
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

    public String getDefaultReasonCode() {
        return "no_change";
    }

    public String generateExceptionCode(WindowItemDTO window, List<WindowItemDTO> googleBusinessHours) {
        boolean isValid = googleBusinessHours != null && googleBusinessHours.size() > 0;
        if (isValid) {
            boolean isAtleastOneBusinessHourExist = false;
            for (WindowItemDTO businessHour : googleBusinessHours) {
                if (businessHour.getFrom() != null && businessHour.getTo() != null) {
                    isAtleastOneBusinessHourExist = true;
                    break;
                }
            }
            // Check at least one google window exist for the validation
            isValid = isAtleastOneBusinessHourExist;
        }
        // check if the window exist for the calculation
        isValid = isValid && window != null && window.getFrom() != null && window.getTo() != null;
        if (!isValid) {
            // Unable to perform the validation
            return "level_4";
        } else {
            int[] array = new int[25];
            for (WindowItemDTO windowItemDTO : googleBusinessHours) {
                generateCalculationArray(windowItemDTO, array, 1);
            }
            generateCalculationArray(window, array, 0);
            int total = 0;
            boolean flag = true;
            int diff = 0;
            for (int i=1; i < array.length; i++) {
                total += array[i];
                if (array[i]==0 && array[i-1]!=0){
                    flag = !flag;
                } else if(flag){
                    diff+=array[i];
                } else {
                    diff-=array[i];
                }
            }
            if (total == 0) {
                return "level_3";
            } else if (total <= 2) {
                if (diff > 0) {
                    return "level_2";
                } else {
                    return "level_2";
                }
            } else {
                if (diff > 0) {
                    return "level_1";
                } else {
                    return "level_1";
                }
            }
        }
    }

    private void generateCalculationArray(WindowItemDTO windowItemDTO, int[] array, int value) {
        int fromHour = Math.max(0, Integer.parseInt(windowItemDTO.getFrom().split(":")[0]));
        int fromMin = Integer.parseInt(windowItemDTO.getFrom().split(":")[1]);
        fromHour = (fromHour == 0 && fromMin == 0) ? 24 : fromHour;
        int toHour = Math.min(Integer.parseInt(windowItemDTO.getTo().split(":")[0]), 24);
        int toMin = Integer.parseInt(windowItemDTO.getTo().split(":")[1]);
        toHour = (toHour == 0 && toMin == 0) ? 24 : toHour;
        for (int i=fromHour; i <= toHour; i++) {
            array[i] = value;
        }
    }

    public List<WindowItemDTO> getSuggestedWindow() {
        return new ArrayList<>();
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

    public Map<String, OpCoDetailsDTO> readOpCoDataFile(String fileName) {
        Map<String, OpCoDetailsDTO> map;
        try {
            Resource resource = new ClassPathResource(fileName);
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

    public String keyGenerator(String opcoId, String customerId) {
        return opcoId + "-" + customerId;
    }

}
