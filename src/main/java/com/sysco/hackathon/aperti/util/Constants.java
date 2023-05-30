package com.sysco.hackathon.aperti.util;

import java.util.Arrays;
import java.util.List;

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
}
