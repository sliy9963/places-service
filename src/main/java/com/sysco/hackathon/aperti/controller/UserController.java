package com.sysco.hackathon.aperti.controller;

import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerDetailsDTO>> getCustomersForOpCo(@RequestParam("opco") String opCoId) {
        LOGGER.info("[UserController] Request received to fetch customer info for OpCo ID: {}", opCoId);
        return new ResponseEntity<>(userService.getCustomersForOpCoGiven(opCoId), HttpStatus.OK);
    }
}
