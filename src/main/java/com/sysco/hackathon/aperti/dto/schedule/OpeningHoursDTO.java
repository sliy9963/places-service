package com.sysco.hackathon.aperti.dto.schedule;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpeningHoursDTO {
    List<PeriodDTO> periods;
}
