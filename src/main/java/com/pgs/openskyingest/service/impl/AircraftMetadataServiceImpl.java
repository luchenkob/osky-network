package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AircraftMetadataServiceImpl implements AircraftMetadataService {

    private final static Logger logger = LoggerFactory.getLogger(AircraftMetadataServiceImpl.class);

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private AircraftPositionRepository aircraftPositionRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Override
    public int insertWatchingAircaftConfig(String... icao24s) {
        logger.info("Adding config of {} aircrafts", icao24s.length);

        // OpenSky seem that does not allow more than 2 concurrent request from the same client
        ExecutorService executor = Executors.newFixedThreadPool(2);

        List<AircraftMetadata> listAircrafts = new ArrayList<>();
        for (String icao24 : icao24s) {
            Runnable runnable = () -> {
                if (aircraftMetadataRepository.findAircraftMetadataByIcao24(icao24) == null) {
                    AircraftMetadata aircraftMetadata = openSkyIntegrationService.getMetadataOfAircraft(icao24);
                    if (aircraftMetadata != null) {
                        aircraftMetadata.setIsTracking(Boolean.TRUE);
                        listAircrafts.add(aircraftMetadata);
                    }
                } else {
                    logger.info("{} had existed in database", icao24);
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
    @Cacheable("retrieveAircraftMetadataByRegistration")
    public List<AircraftMetadata> retrieveAircraftMetadataByRegistration(String registration, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return aircraftMetadataRepository.findAircraftMetadataByRegistrationContains(registration, pageable);
    }

    @Override
    @Cacheable("retrieveAllAircraft")
    public List<AircraftMetadata> retrieveAllAircraft(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AircraftMetadata> aircraftMetadataPage = aircraftMetadataRepository.findAll(pageable);

        return aircraftMetadataPage.getContent();
    }

    @Override
    public Long deleteAircraft(String icao24) {
        return aircraftMetadataRepository.deleteAircraftMetadataByIcao24(icao24)
                + aircraftFlightRepository.deleteAircraftFlightByIcao24(icao24)
                + aircraftPositionRepository.deleteAircraftPositionByIcao24(icao24);

    }

    @Override
    @Cacheable("isCao24Exist")
    public boolean isIcao24Exist(String icao24) {
        return aircraftMetadataRepository.existsByIcao24(icao24);
    }

}
