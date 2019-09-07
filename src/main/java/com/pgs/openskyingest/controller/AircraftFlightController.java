package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AircraftFlightController {

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/aircraft/{icao24}/flight", method = RequestMethod.GET)
    public List<AircraftFlight> getAllPositionOfAircraftFrom(@PathVariable String icao24,
                                                             @RequestParam(value = "from") Long fromTimestamp,
                                                             @RequestParam(value = "to") Long toTimestamp) {
        return aircraftFlightService.retrieveAircraftFlightInTime(icao24, fromTimestamp, toTimestamp);
    }

}
