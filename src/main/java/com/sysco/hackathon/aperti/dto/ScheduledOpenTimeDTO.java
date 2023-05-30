package com.sysco.hackathon.aperti.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledOpenTimeDTO {
    @JsonProperty("SUN")
    String SUN;
    @JsonProperty("MON")
    String MON;
    @JsonProperty("TUE")
    String TUE;
    @JsonProperty("WED")
    String WED;
    @JsonProperty("THU")
    String THU;
    @JsonProperty("FRI")
    String FRI;
    @JsonProperty("SAT")
    String SAT;
}
