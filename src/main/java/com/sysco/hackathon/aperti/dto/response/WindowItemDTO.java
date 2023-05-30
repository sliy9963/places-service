package com.sysco.hackathon.aperti.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WindowItemDTO {
    @Builder.Default
    String from = null;
    @Builder.Default
    String to = null;
}
