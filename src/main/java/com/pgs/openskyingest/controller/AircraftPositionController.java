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

@CrossOrigin(origins = { "http://localhost:3000", "http://ui.opensky-ingest.xyz"})
@RestController
public class AircraftPositionController {

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @RequestMapping(value = "/aircraft/{tailNumberWithIcao24}/position", method = RequestMethod.GET)
    public List<AircraftPosition> getAllPositionOfAircraftFrom(@PathVariable String tailNumberWithIcao24,
                                                               @RequestParam(value = "from") Long fromTimestamp,
                                                               @RequestParam(value = "to") Long toTimestamp) {
        return aircraftPositionService.retrieveAircraftPositionInTime(tailNumberWithIcao24.toUpperCase(), fromTimestamp, toTimestamp);
    }

    @RequestMapping(value = "/aircraft/position/current", method = RequestMethod.GET)
    public List<AircraftPosition> getCurrentPositionOfAllAircraft() {
        return aircraftPositionService.retrieveCurrentPositionOfAllAircraft();
    }

    @RequestMapping(value = "/aircraft/position/latest", method = RequestMethod.GET)
    public List<AircraftPosition> getLatestPositionOfAllAircraft(@RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "size") int size) {
        return aircraftPositionService.retrieveLatestPositionOfAllAircraft(page, size);

    }

    @RequestMapping(value = "/aircraft/position/latest/{tailNumberWithIcao24}", method = RequestMethod.GET)
    public List<AircraftPosition> getLatestPositionOfAircraft(@PathVariable String tailNumberWithIcao24) {
        return aircraftPositionService.retrieveLatestPositionOfAircraft(tailNumberWithIcao24);
    }
}
