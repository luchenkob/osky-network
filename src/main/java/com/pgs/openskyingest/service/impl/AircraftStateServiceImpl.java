package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.service.AircraftStateService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class AircraftStateServiceImpl implements AircraftStateService {

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private AircraftPositionRepository aircraftPositionRepository;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Override
    public void getCurrentStateVectorOfWatchingAircrafts() {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        getAllWatchingAircrafts().forEach(icao24 -> {
            Runnable runnable = () -> {
                List<AircraftPosition> positions = openSkyIntegrationService.getAllStateVectorOfAircraft(icao24, Instant.now().getEpochSecond());
                aircraftPositionRepository.saveAll(positions);
            };

            executor.execute(runnable);
        });

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private List<String> getAllWatchingAircrafts() {
        return aircraftMetadataRepository
                .findAircraftMetadataByIsTracking(Boolean.TRUE)
                .stream()
                .map(meta -> meta.getIcao24())
                .collect(Collectors.toList());
    }
}
