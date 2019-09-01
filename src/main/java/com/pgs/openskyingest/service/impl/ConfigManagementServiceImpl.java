package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.constant.Constant;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.ConfigManagmentService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ConfigManagementServiceImpl implements ConfigManagmentService {

    private final static Logger logger = LoggerFactory.getLogger(ConfigManagementServiceImpl.class);

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Override
    public int insertWatchingAircaftConfig(String... tailNumbers) {
        logger.info("Adding config of {} aircrafts", tailNumbers.length);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<AircraftMetadata> listAircrafts = new ArrayList<>();
        for (String tailNumber : tailNumbers) {
            Runnable runnable = () -> {
                String icao24 = openSkyIntegrationService.getIcao24FromTailNumber(tailNumber);
                if (!Constant.ICAO24_NOT_FOUND.equalsIgnoreCase(icao24)) {
                    AircraftMetadata aircraftMetadata = openSkyIntegrationService.getMetadataOfAircraft(icao24);

                    if (aircraftMetadata != null) {
                        aircraftMetadata.setIsTracking(Boolean.TRUE);
                        listAircrafts.add(aircraftMetadata);
                    }
                }
            };
            executor.execute(runnable);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        logger.info("Saving {} aircrafts metadata", listAircrafts.size());
        if (listAircrafts.size() > 0) {
            aircraftMetadataRepository.saveAll(listAircrafts);
        }

        return listAircrafts.size();
    }

}
