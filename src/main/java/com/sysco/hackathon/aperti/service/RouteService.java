package com.sysco.hackathon.aperti.service;

import com.sysco.hackathon.aperti.dto.OpCoDTO;
import com.sysco.hackathon.aperti.dto.OpCoDetailsDTO;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {

    private ApiUtils apiUtils;

    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    public List<OpCoDTO> getOpCos() {
        List<OpCoDTO> opcoList = new ArrayList<>();
        try {
            Map<String, OpCoDetailsDTO> opcoMap = apiUtils.readOpCoDataFile();
            for (Map.Entry<String, OpCoDetailsDTO> entry : opcoMap.entrySet()) {
                OpCoDTO opco = OpCoDTO.builder().id(entry.getKey()).name(entry.getValue().getName()).build();
                opcoList.add(opco);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while generating opco list: " + e);
        }
        return opcoList;
    }
}
