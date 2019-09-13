package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, List<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumber, Long fromTimestamp, Long toTimestamp) {
        Map<String, List<AircraftFlight>> retData = new HashMap<>();
        List<AircraftFlight> aircraftFlights = retrieveAircraftFlightInTime(tailNumber, fromTimestamp, toTimestamp);

        for (AircraftFlight flight : aircraftFlights) {
            flight.setTailNumber(tailNumber);
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
}
