package com.pgs.openskyingest;

import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UpdatePositionOfWatchingAircraftsTask {
    private static final Logger logger = LoggerFactory.getLogger(UpdatePositionOfWatchingAircraftsTask.class);

    @Autowired
    private AircraftPositionRepository aircraftPositionRepository;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Scheduled(fixedRate = 2 * 60 * 1000)  // 2m
    public void updatePositionOfWatchingAircrafts() {
        logger.info("Trigger getting and updating all watching aircraft...");

        List<AircraftPosition> livingPositionOfAircrafts = openSkyIntegrationService.getAllCurrentStateVector();
        logger.info("opensky return {} records of all state vectors", livingPositionOfAircrafts.size());

        List<AircraftPosition> willInsertToMongo = Collections.synchronizedList( new ArrayList() );

        ExecutorService executor = Executors.newFixedThreadPool(16);
        for (AircraftPosition position : livingPositionOfAircrafts) {
            executor.execute(() -> {
                boolean icao24Existed = aircraftMetadataRepository.existsByIcao24(position.getIcao24());
                if (icao24Existed) {
                    // update list
                    willInsertToMongo.add(position);
                }
            });
        }
        executor.shutdown();
        while(!executor.isTerminated()) {
            //waiting threads finish running
        }

        if (!willInsertToMongo.isEmpty()) {
            logger.info("saving {} aircrafts positions", willInsertToMongo.size());
            aircraftPositionRepository.saveAll(willInsertToMongo);
        } else {
            logger.info("saving 0 aircrafts positions");
        }
    }

    // TODO: we also need fill in missing fields of aircraft metadata
}
