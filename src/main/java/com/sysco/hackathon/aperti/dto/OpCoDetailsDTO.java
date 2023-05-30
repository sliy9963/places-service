package com.sysco.hackathon.aperti.dto;

import lombok.Data;

@Data
public class OpCoDetailsDTO {
    String opcoId;
    String timezone;
    String name;
    AddressDTO address;
}
