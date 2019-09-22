package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AirportMetadata;
import com.pgs.openskyingest.service.AirportMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com"})
@RestController
public class AirportMetadataController {

    @Autowired
    private AirportMetadataService airportMetadataService;

    @RequestMapping(value = "/airport/metadata", method = RequestMethod.GET)
    public List<AirportMetadata> getAirportMetadata(@RequestParam(name = "gpsCode", defaultValue = "") String gpsCode,
                                                    @RequestParam(name = "q", defaultValue = "") String query) {
        if (!gpsCode.isEmpty()) {
            return airportMetadataService.retrieveAirportMetadata(gpsCode.toUpperCase());
        } else if (!query.isEmpty()) {
            return airportMetadataService.retrieveAirportMetadataByUserInput(query);
        } else {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = "/airport/{gpsCode}/departureflights", method = RequestMethod.GET)
    public List<AircraftFlight> getAllFlightsDeparture(@PathVariable String gpsCode) {
        return airportMetadataService.retrieveAllFlightsDepartureAt(gpsCode.toUpperCase());
    }

    @RequestMapping(value = "/airport/{gpsCode}/arriveflights", method = RequestMethod.GET)
    public List<AircraftFlight> getAllFlightsArrive(@PathVariable String gpsCode) {
        return airportMetadataService.retrieveAllFlightsArriveTo(gpsCode.toUpperCase());
    }
}
