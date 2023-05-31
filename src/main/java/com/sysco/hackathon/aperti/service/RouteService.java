package com.sysco.hackathon.aperti.service;

import com.sysco.hackathon.aperti.dto.OpCoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.sysco.hackathon.aperti.util.Constants.opcoMap;

@Service
public class RouteService {

    public List<OpCoDTO> getOpCos() {
        List<OpCoDTO> opcoList;
        try {
            opcoList = opcoMap.entrySet().stream()
                .map(
                    opco -> OpCoDTO.builder().id(opco.getKey()).name(opco.getValue().getName()).build()
                )
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while generating opco list: " + e);
        }
        return opcoList;
    }
}
