package com.pgs.openskyingest.service.impl;

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
import java.util.List;
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
    private AircraftMetadataService aircraftMetadataService;

    @Autowired
    private AircraftPositionRepository aircraftPositionRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Autowired
    private AirportMetadataRepository airportMetadataRepository;

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

    @Override
    public Long numberOfRecords() {
        return aircraftPositionRepository.count();
    }

}
