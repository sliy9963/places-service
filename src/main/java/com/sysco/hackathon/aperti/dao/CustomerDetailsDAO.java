package com.sysco.hackathon.aperti.dao;


import com.sysco.hackathon.aperti.dto.response.WindowDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customerDetails")
public class CustomerDetailsDAO {
    @Id
    @Field(write = Field.Write.ALWAYS, name = "_id")
    String id;
    @Field(write = Field.Write.ALWAYS)
    String opcoId;
    @Field(write = Field.Write.ALWAYS)
    String customerId;
    @Field(write = Field.Write.ALWAYS)
    String shopName;
    @Field(write = Field.Write.ALWAYS)
    @Builder.Default
    List<WindowDTO> windows = new ArrayList<>();
}
