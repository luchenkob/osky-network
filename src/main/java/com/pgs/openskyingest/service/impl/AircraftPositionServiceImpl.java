package com.pgs.openskyingest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.model.AirportMetadata;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.repository.AirportMetadataRepository;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.AircraftPositionService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @Autowired
    private AirportMetadataRepository airportMetadataRepository;

    @Autowired
    private AircraftMetadataService aircraftMetadataService;

    @Override
    public List<AircraftPosition> retrieveAircraftPositionInTime(String tailNumberWithIcao24, Long fromTime, Long toTime) {
        Pattern pattern = Pattern.compile("(.*)\\((.*)\\)");
        Matcher matcher = pattern.matcher(tailNumberWithIcao24);

        String icao24 = "";

        while (matcher.find()) {
            icao24 = matcher.group(2).toLowerCase();
        }

        return aircraftPositionRepository.findAircraftPositionsByIcao24EqualsAndTimePositionBetween(icao24, fromTime, toTime);
    }

    @Override
    public List<AircraftPosition> retrieveCurrentPositionOfAllAircraft() {
        List<AircraftPosition> currentAircraftPositionList = openSkyIntegrationService.getAllCurrentStateVector();
        List<AircraftPosition> returnList = Collections.synchronizedList( new ArrayList() );

        ExecutorService executor = Executors.newFixedThreadPool(6);
        for (AircraftPosition position : currentAircraftPositionList) {
            executor.execute(() -> {
                if (aircraftMetadataService.isIcao24Exist(position.getIcao24())) {
                    // update list
                    returnList.add(position);
                }
            });
        }
        executor.shutdown();
        while(!executor.isTerminated()) {
            //waiting threads finish running
        }

        return returnList;
    }

    @Override
    public List<AircraftPosition> retrieveLatestPositionOfAllAircraft(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<AircraftPosition> aircraftPositions = aircraftPositionRepository.findLastestPositionOfAllAircraft(pageable);

        // fill tail Number
        ExecutorService executor = Executors.newFixedThreadPool(size);
        for (AircraftPosition aircraftPosition : aircraftPositions) {
            Runnable runnable = () -> {
                AircraftMetadata aircraftMetadata = aircraftMetadataRepository.findAircraftMetadataByIcao24(aircraftPosition.getIcao24());
                aircraftPosition.setTailNumber(aircraftMetadata.getRegistration());
                AircraftFlight aircraftFlight = aircraftFlightRepository.findAircraftFlightByIcao24AndFirstSeenLessThanEqualAndLastSeenGreaterThanEqual(aircraftPosition.getIcao24(), aircraftPosition.getTimePosition(), aircraftPosition.getTimePosition());

                if (aircraftFlight != null && aircraftFlight.getEstArrivalAirport() != null) {
                    List<AirportMetadata> airportMetadataList = airportMetadataRepository.findAirportMetadataByGpsCode(aircraftFlight.getEstArrivalAirport());
                    if (!airportMetadataList.isEmpty()) {
                        AirportMetadata airportMetadata = airportMetadataList.get(0);
                        aircraftPosition.setAirport(airportMetadata.getName());
                        aircraftPosition.setAirportRegion(airportMetadata.getIsoRegion());
                    }
                }
            };

            executor.execute(runnable);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            // waiting..
        }

        return aircraftPositions;
    }

    // Since we will have ~300k aircrafts, we won't use this method any more.
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

    private Map<String, String> parseTailNumberAndIcao24(List<String> jsonRets) {
        Map<String, String> icao24WithRegistration = new LinkedHashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (String json : jsonRets) {
            try {
                String icao24 = objectMapper.readTree(json).get("icao24").textValue();
                String registration = objectMapper.readTree(json).get("registration").textValue();
                icao24WithRegistration.put(icao24, registration);
            } catch (Exception e) {
                // do nothing
            }
        }
        return icao24WithRegistration;
    }
}
