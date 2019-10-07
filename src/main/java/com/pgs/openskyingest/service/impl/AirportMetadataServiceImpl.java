package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AirportMetadata;
import com.pgs.openskyingest.repository.AirportMetadataRepository;
import com.pgs.openskyingest.service.AirportMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportMetadataServiceImpl implements AirportMetadataService {

    @Autowired
    private AirportMetadataRepository airportMetadataRepository;

    @Override
    public List<AirportMetadata> retrieveAirportMetadataByUserInput(String query) {
        return airportMetadataRepository.findAirportMetadataByNameContainsIgnoreCase(query);
    }

    @Override
    public List<AirportMetadata> retrieveAirportMetadataByGpsCode(String gpsCode) {
        return airportMetadataRepository.findAirportMetadataByGpsCode(gpsCode);
    }

    @Override
    public Long numberOfRecords() {
        return airportMetadataRepository.count();
    }
}
