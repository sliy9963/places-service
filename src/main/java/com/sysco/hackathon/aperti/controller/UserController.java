package com.sysco.hackathon.aperti.controller;

import com.sysco.hackathon.aperti.dto.response.CustomerDetailsDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;
import com.sysco.hackathon.aperti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerDetailsDTO>> getCustomersForOpCo(@RequestParam("opco") String opCoId) {
        return new ResponseEntity<>(userService.getCustomersForOpCoGiven(opCoId), HttpStatus.OK);
    }
}
