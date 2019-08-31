package com.pgs.openskyingest.service;

public interface ConfigManagmentService {
    /**
     * Insert aircaft need to be watching position real-time
     * This function will do:
     * 1. take tailNumber of aircraft
     * 2. invoke opensky api to get icao24 corresponding to this tailNumber
     * 3. after having icao24, we'll invoke get metadata of aircraft
     * @param tailNumber
     * @return number of document had been inserted
     */
    int insertWatchingAircaftConfig(String tailNumber) ;
}
