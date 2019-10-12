package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;

import java.util.List;

public interface AircraftMetadataService {
    /**
     * Insert aircaft need to be watching position real-time
     * This function will do:
     * 1. take tailNumber of aircraft
     * 2. invoke opensky api to get icao24 corresponding to this tailNumber
     * 3. after having icao24, we'll invoke get metadata of aircraft
     * 4. persist all metadata aircrafts to mongodb
     * @param icao24s
     * @return number of document had been inserted
     */
    int insertWatchingAircaftConfig(String... icao24s) ;

    AircraftMetadata retrieveAircraftMetadataByIcao24(String icao24);

    List<AircraftMetadata> retrieveAircraftMetadataByRegistration(String registration, int page, int size);

    List<AircraftMetadata> retrieveAllAircraft(int page, int size);

    List<AircraftMetadata> retrieveAll();

    Long deleteAircraft(String icao24);

    boolean isIcao24Exist(String icao24);

    Long numberOfRecords();

    String getAircraftOwner(String icao24);

    String getAircraftTailNumber(String icao24);
}
