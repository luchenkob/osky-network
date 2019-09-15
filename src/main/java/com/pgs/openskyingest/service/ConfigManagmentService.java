package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;

import java.util.List;

public interface ConfigManagmentService {
    /**
     * Insert aircaft need to be watching position real-time
     * This function will do:
     * 1. take tailNumber of aircraft
     * 2. invoke opensky api to get icao24 corresponding to this tailNumber
     * 3. after having icao24, we'll invoke get metadata of aircraft
     * 4. persist all metadata aircrafts to mongodb
     * @param tailNumbers
     * @return number of document had been inserted
     */
    int insertWatchingAircaftConfig(String... tailNumbers) ;

    AircraftMetadata retrieveAircraftMetadataByIcao24(String icao24);

    AircraftMetadata retrieveAircraftMetadataByRegistration(String registration);

    List<AircraftMetadata> retrieveAllAircraft();

    String[] retrieveAllAircraftTailNumber();
}
