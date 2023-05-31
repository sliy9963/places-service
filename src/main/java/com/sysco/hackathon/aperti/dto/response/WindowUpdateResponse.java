package com.sysco.hackathon.aperti.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WindowUpdateResponse {
    String message;
    String opcoId;
    String customerId;
}
