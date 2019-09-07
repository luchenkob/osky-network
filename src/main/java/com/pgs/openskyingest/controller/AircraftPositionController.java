package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.service.AircraftPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AircraftPositionController {

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @RequestMapping(value = "/aircraft/{icao24}/position", method = RequestMethod.GET)
    public List<AircraftPosition> getAllPositionOfAircraftFrom(@PathVariable String icao24,
                                                               @RequestParam(value = "from") Long fromTimestamp,
                                                               @RequestParam(value = "to") Long toTimestamp) {
        return aircraftPositionService.retrieveAircraftPositionInTime(icao24, fromTimestamp, toTimestamp);
    }

    @RequestMapping(value = "/aircraft/{icao24}/position/current", method = RequestMethod.GET)
    public List<AircraftPosition> getAllPositionOfAircraftFrom(@PathVariable String icao24) {
        return aircraftPositionService.retrieveCurrentAircraftPosition(icao24);
    }
}
