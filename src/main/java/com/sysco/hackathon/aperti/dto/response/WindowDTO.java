package com.sysco.hackathon.aperti.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WindowDTO {
    Integer day;
    WindowItemDTO window;
    WindowItemDTO googleBusinessHours;
    String reasonCode;
    String exception;
}
