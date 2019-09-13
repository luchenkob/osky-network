package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com" })
@RestController
public class AircraftCompareController {

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/aircraft/compare", method = RequestMethod.GET)
    public Map<String, List<AircraftFlight>> getAircraftsCompare(@RequestParam(value = "aircrafts") String aircrafts,
                                                           @RequestParam(value = "from") Long from,
                                                           @RequestParam(value = "to") Long to) {
        String[] tailNumbers = aircrafts.toUpperCase().split(",");
        Map<String, List<AircraftFlight>> retData = new HashMap<>();
        for (String tailNumber : tailNumbers) {
            Map<String, List<AircraftFlight>> flightGroupByDate = aircraftFlightService.retrieveAircraftFlightGroupByDate(tailNumber, from, to);
            for (String date : flightGroupByDate.keySet()) {
                if (retData.get(date) == null) {
                    retData.put(date, flightGroupByDate.get(date));
                } else {
                    retData.get(date).addAll(flightGroupByDate.get(date));
                }
            }
        }

        return retData;
    }

}
