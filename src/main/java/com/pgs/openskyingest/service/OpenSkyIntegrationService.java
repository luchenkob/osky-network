package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface OpenSkyIntegrationService {
    String getIcao24FromTailNumber(String tailNumber);
    AircraftMetadata getMetadataOfAircraft(String icao24);
    List<AircraftPosition> getAllStateVectorOfAircraft(String icao24, Long timestamp);
}
