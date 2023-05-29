package com.sysco.hackathon.aperti.util;

import com.google.maps.GeoApiContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.sysco.hackathon.aperti.util.Constants.USER_DATA_QUERY_FORMAT;
import static com.sysco.hackathon.aperti.util.Constants.USER_DATA_QUERY_JOIN;

@Component
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ApiUtils {


    @Value("${application.google.api.key}")
    private String apiKey;

    @Value("${application.customer.service.url}")
    private String customerServiceUrl;

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

}
