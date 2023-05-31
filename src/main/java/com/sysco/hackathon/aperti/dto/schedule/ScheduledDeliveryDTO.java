package com.sysco.hackathon.aperti.dto.schedule;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledDeliveryDTO {
    String customer;
    String opco;
    @JsonProperty("deliveryWindow")
    ScheduledDeliveryWindowDTO deliveryWindow;
}
