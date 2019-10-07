package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AirportMetadata;

import java.util.List;

public interface AirportMetadataService {

    List<AirportMetadata> retrieveAirportMetadata(String gpsCode);

    List<AirportMetadata> retrieveAirportMetadataByUserInput(String query);

    List<AirportMetadata> retrieveAirportMetadataByGpsCode(String gpsCode);

    Long numberOfRecords();
}
