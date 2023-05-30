package com.sysco.hackathon.aperti.controller;

import com.sysco.hackathon.aperti.dto.OpCoDTO;
import com.sysco.hackathon.aperti.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RouteController {

    private RouteService routeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    public void setRouteService(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping(path = "/opcos")
    public ResponseEntity<List<OpCoDTO>> getOpCoList() {
        LOGGER.info("Request received to fetch opcos [RouteController]: {}", UUID.randomUUID());
        return new ResponseEntity<>(routeService.getOpCos(), HttpStatus.OK);
    }
}
