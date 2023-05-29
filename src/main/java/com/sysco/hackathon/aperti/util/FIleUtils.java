package com.sysco.hackathon.aperti.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class FIleUtils {

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public Object getOpCoList() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:opcoDetails.json");
        File file = resource.getFile();
        Map<String, Object> userData = objectMapper.readValue(
                file
                , new TypeReference<Map<String, Object>>() {
                });
        System.out.println(userData);
        return null;
    }
}
