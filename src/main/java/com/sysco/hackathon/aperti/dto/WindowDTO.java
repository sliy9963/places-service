package com.sysco.hackathon.aperti.dto;

import lombok.Data;

@Data
public class WindowDTO {
    Integer day;
    String reasonCode;
    Object window;
    Object googleBusinessHours;
}
