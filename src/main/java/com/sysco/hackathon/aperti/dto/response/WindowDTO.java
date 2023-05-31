package com.sysco.hackathon.aperti.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WindowDTO {
    String day;
    WindowItemDTO window;
    List<WindowItemDTO> googleBusinessHours;
    String reasonCode;
    String exception;
}
