package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.service.AircraftPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com"})
@RestController
public class AircraftPositionController {

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @RequestMapping(value = "/aircraft/{tailNumber}/position", method = RequestMethod.GET)
    public List<AircraftPosition> getAllPositionOfAircraftFrom(@PathVariable String tailNumber,
                                                               @RequestParam(value = "from") Long fromTimestamp,
                                                               @RequestParam(value = "to") Long toTimestamp) {
        return aircraftPositionService.retrieveAircraftPositionInTime(tailNumber, fromTimestamp, toTimestamp);
    }

    @RequestMapping(value = "/aircraft/{tailNumber}/position/current", method = RequestMethod.GET)
    public List<AircraftPosition> getAllPositionOfAircraftFrom(@PathVariable String tailNumber) {
        return aircraftPositionService.retrieveCurrentAircraftPosition(tailNumber);
    }
}
