package com.sysco.hackathon.aperti.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerDetailsDTO {
    String opcoId;
    String customerId;
    String shopName;
    List<WindowDTO> windows;
}
