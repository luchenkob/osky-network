package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AircraftFlightServiceImpl implements AircraftFlightService {

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Override
    public List<AircraftFlight> retrieveAircraftFlightInTime(String tailNumber, Long fromTimestamp, Long toTimestamp) {
        String icao24 = aircraftMetadataRepository.findAircraftMetadataByRegistration(tailNumber).getIcao24();
        return aircraftFlightRepository.findAircraftFlightByIcao24EqualsAndFirstSeenBetween(icao24, fromTimestamp, toTimestamp);
    }

    @Override
    public Map<String, Set<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumber, Long fromTimestamp, Long toTimestamp) {
        Map<String, Set<AircraftFlight>> retData = new HashMap<>();
        List<AircraftFlight> aircraftFlights = retrieveAircraftFlightInTime(tailNumber, fromTimestamp, toTimestamp);

        for (AircraftFlight flight : aircraftFlights) {
            flight.setTailNumber(tailNumber);
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(flight.getFirstSeen() * 1000));

            if (retData.get(date) == null) {
                Set<AircraftFlight> ls = new HashSet<>();
                ls.add(flight);
                retData.put(date, ls);
            } else {
                retData.get(date).add(flight);
            }
        }

        return retData;
    }

    @Override
    public Map<String, Set<AircraftFlightCompare>> retrieveAircraftsFlightGroupByDate(String[] tailNumbers, Long from, Long to) {
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
            Map<String, Set<AircraftFlight>> flightGroupByDate = retrieveAircraftFlightGroupByDate(tailNumber, from, to);

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
                    aircraftFlightCompare.setDeparture(flights.stream().map(f -> String.valueOf(f.getFirstSeen())).collect(Collectors.joining(",")));
                    aircraftFlightCompare.setArrival(flights.stream().map(f -> String.valueOf(f.getLastSeen())).collect(Collectors.joining(",")));
                    aircraftFlightCompare.setIcao24(flights.stream().map(f -> String.valueOf(f.getIcao24())).collect(Collectors.joining(",")));
                    aircraftFlightCompare.setTailNumber(tailNumber);

                    retData.get(dateInRetData).add(aircraftFlightCompare);
                }
            }

        }

        return retData;
    }
}
