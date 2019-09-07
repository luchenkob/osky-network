package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface AircraftPositionService {

    List<AircraftPosition> retrieveAircraftPositionInTime(String icao24, Long fromTimestamp, Long toTimestamp);

    /**
     * Just invoke opensky api /track with time = 0
     * @param icao24
     * @return
     */
    List<AircraftPosition> retrieveCurrentAircraftPosition(String icao24);

    /**
     * This method will do:
     *  1. Identify flights which are latest flights
     *  2. On each new flights, get tracking position
     * Notice: It will be invoked as schedule task
     * @param icao24
     */
    void updateAircraftPositionFromFlight(String icao24);
}
