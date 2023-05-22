package com.sysco.hackathon.aperti.util;

import com.google.maps.GeoApiContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiUtils {


    @Value("${application.google.api.key}")
    private String apiKey;

    public GeoApiContext getContext() {
        return new GeoApiContext.Builder()
            .apiKey(apiKey)
                .build();
    }

    public String getEncodedText(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
