package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = { "http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com" })
@RestController
public class AircraftCompareController {

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @RequestMapping(value = "/aircraft/compare", method = RequestMethod.GET)
    public Map<String, Set<AircraftFlightCompare>> getAircraftsCompare(@RequestParam(value = "aircrafts") String aircrafts,
                                                                       @RequestParam(value = "from") Long from,
                                                                       @RequestParam(value = "to") Long to) {
        String[] tailNumbers = aircrafts.toUpperCase().split(",");
        Map<String, Set<AircraftFlightCompare>> retData = new HashMap<>();

        // init map
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fromDate = Calendar.getInstance();
        fromDate.setTimeInMillis(from * 1000);

        Calendar toDate = Calendar.getInstance();
        toDate.setTimeInMillis(to * 1000);

        while (fromDate.before(toDate)) {
            retData.put(sdf.format(fromDate.getTime()), new HashSet<>());
            fromDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        // fill data
        for (String tailNumber : tailNumbers) {
            Map<String, Set<AircraftFlight>> flightGroupByDate = aircraftFlightService.retrieveAircraftFlightGroupByDate(tailNumber, from, to);

            for (String dateInRetData : retData.keySet()) {
                Set<AircraftFlight> flights = flightGroupByDate.get(dateInRetData);
                if (flights == null) {
                    // create blank flight with tailNumber only
                    AircraftFlightCompare aircraftFlightCompare = new AircraftFlightCompare();
                    aircraftFlightCompare.setTailNumber(tailNumber);

                    retData.get(dateInRetData).add(aircraftFlightCompare);
                } else {
                    // merge flights
                    AircraftFlightCompare aircraftFlightCompare = new AircraftFlightCompare();
                    aircraftFlightCompare.setTailNumber(tailNumber);
                    aircraftFlightCompare.setDeparture("");
                    aircraftFlightCompare.setArrival("");
                    aircraftFlightCompare.setIcao24("");

                    flights.forEach(f -> {
                        aircraftFlightCompare.setDeparture(aircraftFlightCompare.getDeparture() + "," + f.getFirstSeen());
                        aircraftFlightCompare.setArrival(aircraftFlightCompare.getArrival() + "," + f.getLastSeen());
                        aircraftFlightCompare.setIcao24(aircraftFlightCompare.getIcao24() + "," + f.getIcao24());
                    });

                    retData.get(dateInRetData).add(aircraftFlightCompare);
                }
            }

        }

        return retData;
    }

}
