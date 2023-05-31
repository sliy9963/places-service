package com.sysco.hackathon.aperti.dto.schedule;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeriodDTO {
    WindowStatusDTO open;
    WindowStatusDTO close;
}
