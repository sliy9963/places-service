package com.sysco.hackathon.aperti.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpCoDTO {
    String id;
    String name;
    @Builder.Default
    boolean value = false;
}
