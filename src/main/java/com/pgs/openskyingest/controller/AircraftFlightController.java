package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "http://ui.opensky-ingest.xyz" })
@RestController
public class AircraftFlightController {

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/aircraft/{tailNumber}/flight", method = RequestMethod.GET)
    public List<AircraftFlight> getAllPositionOfAircraftFrom(@PathVariable String tailNumber,
                                                             @RequestParam(value = "from") Long fromTimestamp,
                                                             @RequestParam(value = "to") Long toTimestamp) {
        return aircraftFlightService.retrieveAircraftFlightInTime(tailNumber.toUpperCase(), fromTimestamp, toTimestamp);
    }

}
