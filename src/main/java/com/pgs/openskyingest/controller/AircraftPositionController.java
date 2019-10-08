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

import java.util.ArrayList;
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
    public List<AircraftPosition> getLatestPositionOfAllAircraft(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", defaultValue = "0") int size,
                                                                 @RequestParam(value = "tailNumber", defaultValue = "") String tailNumbers) {
        if (tailNumbers.isEmpty()) {
            return aircraftPositionService.retrieveLatestPositionOfAllAircraft(page, size);
        } else {
            List<AircraftPosition> positions = new ArrayList<>();
            String[] tailNumbersArray = tailNumbers.split(",");
            for (String tailNumberWithIcao24 : tailNumbersArray) {
                positions.addAll(aircraftPositionService.retrieveLatestPositionOfAircraft(tailNumberWithIcao24));
            }

            return positions;
        }

    }

    @RequestMapping(value = "/aircraft/position/latest/{tailNumberWithIcao24}", method = RequestMethod.GET)
    public List<AircraftPosition> getLatestPositionOfAircraft(@PathVariable String tailNumberWithIcao24) {
        return aircraftPositionService.retrieveLatestPositionOfAircraft(tailNumberWithIcao24);
    }
}
