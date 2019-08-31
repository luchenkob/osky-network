package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;

public interface OpenSkyIntegrationService {
    String getIcao24FromTailNumber(String tailNumber);
    AircraftMetadata getMetadataOfAirCraft(String icao24);
}
