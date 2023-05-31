package com.sysco.hackathon.aperti.util;

import com.sysco.hackathon.aperti.dto.OpCoDetailsDTO;
import com.sysco.hackathon.aperti.dto.sfdc.SfdcCustomerDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {

    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String GRANT_TYPE = "grant_type";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USER_DATA_QUERY_FORMAT = "SELECT+Account_ID__c,Name,ShippingStreet,ShippingCity,ShippingState,ShippingPostalCode,Location__c+FROM+Account+WHERE+Account_ID__c=";
    public static final String USER_DATA_QUERY_JOIN = "+OR+Account_ID__c=";
    public static final String SFDC_API_URL_SEGMENT = "/services/data/v39.0/query?q=";
    public static List<String> exceptionsList = Arrays.asList("level_1", "level_2", "level_3", "level_4");
    public static List<String> reasonCodesList = Arrays.asList("no_change", "update_window", "always_ignore", "contact_customer");

    public enum DayNumberOfWeek {
        Sunday("6"),
        Monday("0"),
        Tuesday("1"),
        Wednesday("2"),
        Thursday("3"),
        Friday("4"),
        Saturday("5");

        DayNumberOfWeek(String value) {
            this.value = value;
        }

        private final String value;

        public String getValue() {
            return value;
        }
    }

    public static final String START_TIME = "01:00";
    public static final String END_TIME = "20:00";

    public static final Map<String, List<SfdcCustomerDTO>> customerMap = new ConcurrentHashMap<>();
    public static final Map<String, OpCoDetailsDTO> opcoMap = new ConcurrentHashMap<>();

}
