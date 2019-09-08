package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.AircraftFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
