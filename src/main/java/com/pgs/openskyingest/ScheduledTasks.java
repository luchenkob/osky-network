package com.pgs.openskyingest;

import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.AircraftPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000)  // 2hrs
    public void updatePositionOfWatchingAircrafts() {
        logger.info("Trigger getting and updating all watching aircraft");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        aircraftMetadataRepository.findAircraftMetadataByIsTracking(Boolean.TRUE).forEach(aircraftMetadata ->
                executor.execute(() -> aircraftPositionService.updateAircraftPositionFromFlight(aircraftMetadata.getIcao24()))
        );

        executor.shutdown();
        while(!executor.isTerminated()) {

        }
    }
}
