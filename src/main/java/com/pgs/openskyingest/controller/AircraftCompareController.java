package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlightCompare;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:3000", "http://ui.opensky-ingest.xyz" })
@RestController
public class AircraftCompareController {

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/aircraft/compare", method = RequestMethod.GET)
    public Map<String, List<AircraftFlightCompare>> getAircraftsCompare(@RequestParam(value = "aircrafts") String aircrafts,
                                                                        @RequestParam(value = "from") Long from,
                                                                        @RequestParam(value = "to") Long to,
                                                                        @RequestParam(value = "clientTz", defaultValue = "UTC") String clientTz) {
        String[] tailNumberWithIcao24s = aircrafts.toUpperCase().split(",");
        return aircraftFlightService.retrieveAircraftsFlightGroupByDate(tailNumberWithIcao24s, from, to, clientTz);
    }

}
