package com.sysco.hackathon.aperti.dto.sfdc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SfdcCustomerDTO {
    AttributeDTO attributes;
    @JsonProperty("Account_ID__c")
    String Account_ID__c;
    @JsonProperty("Name")
    String Name;
    @JsonProperty("ShippingStreet")
    String ShippingStreet;
    @JsonProperty("ShippingCity")
    String ShippingCity;
    @JsonProperty("ShippingState")
    String ShippingState;
    @JsonProperty("ShippingPostalCode")
    String ShippingPostalCode;
    @JsonProperty("Location__c")
    LocationDTO Location__c;
}
