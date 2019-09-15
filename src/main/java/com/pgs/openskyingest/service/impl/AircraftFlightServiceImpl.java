package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class AircraftFlightServiceImpl implements AircraftFlightService {

    private final static Logger logger = LoggerFactory.getLogger(AircraftFlightServiceImpl.class);

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
    public Map<String, List<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumber, Long fromTimestamp, Long toTimestamp, String clientTz) {
        Map<String, List<AircraftFlight>> retData = new LinkedHashMap<>();
        List<AircraftFlight> aircraftFlights = retrieveAircraftFlightInTime(tailNumber, fromTimestamp, toTimestamp);

        for (AircraftFlight flight : aircraftFlights) {
            flight.setTailNumber(tailNumber);
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone(clientTz));

            String date = sdf.format(new Date(flight.getFirstSeen() * 1000));

            if (retData.get(date) == null) {
                List<AircraftFlight> ls = new ArrayList<>();
                ls.add(flight);
                retData.put(date, ls);
            } else {
                retData.get(date).add(flight);
            }
        }

        return retData;
    }

    @Override
    public Map<String, List<AircraftFlightCompare>> retrieveAircraftsFlightGroupByDate(String[] tailNumbers, Long from, Long to, String clientTz) {
        Map<String, List<AircraftFlightCompare>> retData = new LinkedHashMap<>();

        // fill data
        for (String tailNumber : tailNumbers) {
            Map<String, List<AircraftFlight>> flightGroupByDate = retrieveAircraftFlightGroupByDate(tailNumber, from, to, clientTz);

            for (String date : flightGroupByDate.keySet()) {
                List<AircraftFlight> flights = flightGroupByDate.get(date);
                AircraftFlightCompare aircraftFlightCompare = new AircraftFlightCompare(tailNumber);
                aircraftFlightCompare.setDeparture(flights.stream().map(f -> String.valueOf(f.getFirstSeen())).collect(Collectors.joining(",")));
                aircraftFlightCompare.setDepartureAirport(flights.stream().map(f -> f.getEstDepartureAirport()).collect(Collectors.joining(",")));
                aircraftFlightCompare.setArrival(flights.stream().map(f -> String.valueOf(f.getLastSeen())).collect(Collectors.joining(",")));
                aircraftFlightCompare.setArrivalAirport(flights.stream().map(f -> f.getEstArrivalAirport()).collect(Collectors.joining(",")));
                aircraftFlightCompare.setIcao24(flights.stream().map(f -> String.valueOf(f.getIcao24())).collect(Collectors.joining(",")));

                if (retData.get(date) == null) {
                    List<AircraftFlightCompare> afcls = new ArrayList<>();
                    afcls.add(aircraftFlightCompare);
                    retData.put(date, afcls);
                } else {
                    retData.get(date).add(aircraftFlightCompare);
                }
            }
        }

        // fill missing data
        for (Map.Entry<String, List<AircraftFlightCompare>> entry : retData.entrySet()) {
            List<AircraftFlightCompare> newList = new ArrayList<>();
            List<AircraftFlightCompare> currentList = entry.getValue();

            for (String tailNumber : tailNumbers) {
                newList.add(currentList.stream()
                        .filter(afc -> afc.getTailNumber().equalsIgnoreCase(tailNumber))
                        .findFirst()
                        .orElse(new AircraftFlightCompare(tailNumber)));
            }
            //update entry
            entry.setValue(newList);
        }

        // return sorted key
        return retData.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
