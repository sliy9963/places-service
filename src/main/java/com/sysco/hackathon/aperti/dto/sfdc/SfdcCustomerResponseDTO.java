package com.sysco.hackathon.aperti.dto.sfdc;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SfdcCustomerResponseDTO {
    Integer totalSize;
    boolean done;
    List<SfdcCustomerDTO> records;
}
