package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledDeliveryWindowDTO {
    @JsonProperty("storeOpenTimeByDay")
    ScheduledOpenTimeDTO storeOpenTimeByDay;
    @JsonProperty("storeCloseTimeByDay")
    ScheduledOpenTimeDTO storeCloseTimeByDay;
}
