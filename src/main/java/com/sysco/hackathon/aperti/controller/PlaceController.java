package com.sysco.hackathon.aperti.controller;

import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import com.sysco.hackathon.aperti.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PlaceController {

    private PlaceService placeService;


    @Autowired
    public void setPlacesService(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping(path = "/places")
    public ResponseEntity<List<PlacesSearchResult>> getPlaceData(@RequestParam("search") String placeSearch) {
        return new ResponseEntity<>(placeService.getPlaceIds(placeSearch), HttpStatus.OK);
    }

    @GetMapping(path = "/placeDetails")
    public ResponseEntity<List<PlaceDetails>> getPlaceDetails(@RequestParam("search") String placeSearch) {
        return new ResponseEntity<>(placeService.getPlaceDetails(placeSearch), HttpStatus.OK);
    }

    @GetMapping(path = "/places/byLocation")
    public ResponseEntity<List<PlacesSearchResult>> getPlaceDetailsByLocation(
            @RequestParam("search") String placeSearch, @RequestParam("lat") String lat,
            @RequestParam("lon") String lon) {
        return new ResponseEntity<>(placeService.getPlaceDetailsByLocation(placeSearch, lat, lon), HttpStatus.OK);
    }

    @GetMapping(path = "/places/openingHours")
    public ResponseEntity<List<OpeningHours>> getPlaceOpeningHours(@RequestParam("search") String placeSearch) {
        return new ResponseEntity<>(placeService.getPlaceOpeningHours(placeSearch), HttpStatus.OK);
    }

    @GetMapping(path = "/places/{placeId}/openingHours")
    public ResponseEntity<List<OpeningHours>> getPlaceOpeningHoursById(@PathVariable("placeId") String placeId) {
        return new ResponseEntity<>(placeService.getPlaceOpeningHoursById(placeId), HttpStatus.OK);
    }

}
