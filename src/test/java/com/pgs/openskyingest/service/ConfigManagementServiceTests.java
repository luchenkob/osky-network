package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigManagementServiceTests {

    @Autowired
    private ConfigManagmentService configManagmentService;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Test
    public void addConfig() {
        aircraftMetadataRepository.deleteAll();
        configManagmentService.insertWatchingAircaftConfig("N15GX", "N17GX");

        for (AircraftMetadata meta : aircraftMetadataRepository.findAll()) {
            System.out.println(meta);
        }

    }

    @Test
    public void findAllIsTrackingAircraft() {
        configManagmentService.insertWatchingAircaftConfig("N171CL", "N4281K");
        aircraftMetadataRepository.findAircraftMetadataByIsTracking(Boolean.TRUE).forEach(meta -> System.out.println(meta.toString()));
    }
}

