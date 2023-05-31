package com.sysco.hackathon.aperti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WindowItemDTO {
    @Builder.Default
    String from = null;
    @Builder.Default
    String to = null;
}
