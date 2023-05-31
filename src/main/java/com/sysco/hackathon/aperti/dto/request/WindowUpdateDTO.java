package com.sysco.hackathon.aperti.dto.request;

import com.sysco.hackathon.aperti.dto.response.WindowItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class WindowUpdateDTO {
    String day;
    String opcoId;
    String reasonCode;
    String customerId;
    List<WindowItemDTO> suggestedWindow;
}
