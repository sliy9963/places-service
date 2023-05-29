package com.sysco.hackathon.aperti.controller;

import com.sysco.hackathon.aperti.dto.CustomerDTO;
import com.sysco.hackathon.aperti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerDTO>> getCustomersForOpCo(@RequestParam("opco") String opCoId) {
        return new ResponseEntity<>(userService.getCustomersForOpCoGiven(opCoId), HttpStatus.OK);
    }
}
