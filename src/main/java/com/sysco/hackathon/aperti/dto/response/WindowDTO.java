package com.sysco.hackathon.aperti.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WindowDTO {
    String day;
    WindowItemDTO window;
    List<WindowItemDTO> googleBusinessHours;
    String reasonCode;
    String exception;
    @Builder.Default
    List<WindowItemDTO> suggestedWindow = new ArrayList<>();
}
