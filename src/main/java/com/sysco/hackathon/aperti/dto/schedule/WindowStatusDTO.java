package com.sysco.hackathon.aperti.dto.schedule;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WindowStatusDTO {
    String day;
    String time;
}
