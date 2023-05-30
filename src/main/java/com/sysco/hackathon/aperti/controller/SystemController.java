package com.sysco.hackathon.aperti.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class SystemController {
    @GetMapping(path = "")
    public ResponseEntity<String> getHeathCheck() {
        return new ResponseEntity<>("Places API is up and running!", HttpStatus.OK);
    }
}
