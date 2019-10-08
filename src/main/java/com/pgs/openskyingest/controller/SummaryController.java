package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.service.AircraftFlightService;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.AircraftPositionService;
import com.pgs.openskyingest.service.AirportMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


@CrossOrigin(origins = {"http://localhost:3000", "http://ui.opensky-ingest.xyz"})
@RestController
public class SummaryController {

    @Autowired
    private AirportMetadataService airportMetadataService;

    @Autowired
    private AircraftMetadataService aircraftMetadataService;

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public Map<String, Object> getAirportMetadata() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("aircraftMetadata", aircraftMetadataService.numberOfRecords());
        summary.put("aircraftFlight", aircraftFlightService.numberOfRecords());
        summary.put("aircraftPosition", aircraftPositionService.numberOfRecords());
        summary.put("aircraftLatestPosition", aircraftPositionService.numberOfLatestPositionRecords());
        summary.put("airport", airportMetadataService.numberOfRecords());

        return summary;
    }
}