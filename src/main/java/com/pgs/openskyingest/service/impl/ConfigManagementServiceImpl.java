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

        // OpenSky seem that does not allow more than 2 concurrent request from the same client
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<AircraftMetadata> listAircrafts = new ArrayList<>();
        for (String tailNumber : tailNumbers) {
            Runnable runnable = () -> {
                if (aircraftMetadataRepository.findAircraftMetadataByRegistration(tailNumber) == null) {
                    openSkyIntegrationService.getIcao24FromTailNumber(tailNumber).forEach(icao24 -> {
                        if (!Constant.ICAO24_NOT_FOUND.equalsIgnoreCase(icao24)) {
                            AircraftMetadata aircraftMetadata = openSkyIntegrationService.getMetadataOfAircraft(icao24);

                            if (aircraftMetadata != null) {
                                aircraftMetadata.setIsTracking(Boolean.TRUE);
                                listAircrafts.add(aircraftMetadata);
                            }
                        }
                    });
                } else {
                    logger.info("{} had existed in database", tailNumber);
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

    @Override
    public AircraftMetadata retrieveAircraftMetadataByIcao24(String icao24) {
        return aircraftMetadataRepository.findAircraftMetadataByIcao24(icao24);
    }

    @Override
    public AircraftMetadata retrieveAircraftMetadataByRegistration(String registration) {
        return aircraftMetadataRepository.findAircraftMetadataByRegistration(registration);
    }

    @Override
    public List<AircraftMetadata> retrieveAllAircraft() {
        return aircraftMetadataRepository.findAll();
    }

    @Override
    public String[] retrieveAllAircraftTailNumber() {
        return aircraftMetadataRepository.findAllAircraftTailNumber();
    }

}
