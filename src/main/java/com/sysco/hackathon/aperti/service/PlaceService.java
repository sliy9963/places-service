package com.sysco.hackathon.aperti.service;

import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.FindPlaceFromText;
import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import com.sysco.hackathon.aperti.util.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PlaceService {

    private ApiUtils apiUtils;
    private GeoApiContext apiContext;

    @Autowired
    public void setApiUtils(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
        this.apiContext = apiUtils.getContext();
    }

    public List<PlacesSearchResult> getPlaceIds(String placeSearch) {
        List<PlacesSearchResult> places;
        try {
            String encodePlaceSearch = apiUtils.getEncodedText(placeSearch);
            FindPlaceFromText placeFromText = PlacesApi.findPlaceFromText(apiContext, encodePlaceSearch, FindPlaceFromTextRequest.InputType.TEXT_QUERY).await();
            places = Arrays.asList(placeFromText.candidates);
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching place details: " + e.getMessage());
        }
        return places;
    }

    public List<PlaceDetails> getPlaceDetails(String placeSearch) {
        List<PlaceDetails> places = new ArrayList<>();
        try {
            List<PlacesSearchResult> placeList = getPlaceIds(placeSearch);
            if (placeList.size() == 0) return Collections.emptyList();
            for (PlacesSearchResult place : placeList) {
                PlaceDetails placeDetails = PlacesApi.placeDetails(apiContext, place.placeId).await();
                places.add(placeDetails);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching place opening hours: " + e.getMessage());
        }
        return places;
    }

    public List<OpeningHours> getPlaceOpeningHoursById(String placeSearch) {
        List<OpeningHours> openingHoursList = new ArrayList<>();
        try {
            PlaceDetails placeDetails = PlacesApi.placeDetails(apiContext, placeSearch).await();
            if (placeDetails != null && placeDetails.openingHours != null) {
                openingHoursList.add(placeDetails.openingHours);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching place opening hours by place ID: " + e.getMessage());
        }
        return openingHoursList;
    }

    public List<OpeningHours> getPlaceOpeningHours(String placeSearch) {
        List<OpeningHours> openingHoursList = new ArrayList<>();
        try {
            List<PlacesSearchResult> placeList = getPlaceIds(placeSearch);
            if (placeList.size() == 0) return Collections.emptyList();
            for (PlacesSearchResult place : placeList) {
                PlaceDetails placeDetails = PlacesApi.placeDetails(apiContext, place.placeId).await();
                if (placeDetails != null && placeDetails.openingHours != null) {
                    openingHoursList.add(placeDetails.openingHours);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while fetching place opening hours: " + e.getMessage());
        }
        return openingHoursList;
    }

}
