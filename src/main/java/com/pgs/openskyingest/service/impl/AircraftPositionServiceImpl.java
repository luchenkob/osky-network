package com.pgs.openskyingest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.service.AircraftPositionService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class AircraftPositionServiceImpl implements AircraftPositionService {

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private AircraftPositionRepository aircraftPositionRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Override
    public List<AircraftPosition> retrieveAircraftPositionInTime(String tailNumber, Long fromTime, Long toTime) {
        String icao24 = aircraftMetadataRepository.findAircraftMetadataByRegistration(tailNumber).getIcao24();
        return aircraftPositionRepository.findAircraftPositionsByIcao24EqualsAndTimePositionBetween(icao24, fromTime, toTime);
    }

    @Override
    public List<AircraftPosition> retrieveCurrentAircraftPosition(String tailNumber) {
        String icao24 = aircraftMetadataRepository.findAircraftMetadataByRegistration(tailNumber).getIcao24();
        return openSkyIntegrationService.getAllStateVectorOfAircraft(icao24, Instant.now().getEpochSecond());
    }

    @Override
    public List<AircraftPosition> retrieveCurrentPositionOfAllAircraft() {
        List<String> jsonRets = aircraftMetadataRepository.findAllAircraftIcao24();
        List<String> icao24s = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (String json : jsonRets) {
            try {
                String icao24 = objectMapper.readTree(json).get("icao24").textValue();
                icao24s.add(icao24);
            } catch (Exception e) {
                // do nothing
            }
        }
        return openSkyIntegrationService.getAllStateVectorOfMultiAircraft(icao24s);
    }

    @Override
    public void updateAircraftPositionFromFlight(String icao24) {
        // identify latest flights
        Long end = Instant.now().getEpochSecond();
        Long begin = end - 30*24*60*60;

        List<AircraftFlight> flights = openSkyIntegrationService.getFlightsOfAircraft(icao24, begin, end);

        List<AircraftFlight> dbFlights = aircraftFlightRepository.findAircraftFlightByIcao24(icao24);

        List<AircraftFlight> newFlights = flights.stream().filter(flight -> !dbFlights.contains(flight)).collect(Collectors.toList());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        newFlights.forEach(flight ->
            executor.execute(() -> {
                List<AircraftPosition> positions = openSkyIntegrationService.getTrackedPositionOfAircraft(icao24, flight.getFirstSeen());
                aircraftPositionRepository.saveAll(positions);
            })
        );

        executor.execute(() -> aircraftFlightRepository.saveAll(newFlights));

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }
}
