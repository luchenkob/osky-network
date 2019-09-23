package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface AircraftPositionService {

    List<AircraftPosition> retrieveAircraftPositionInTime(String tailNumber, Long fromTimestamp, Long toTimestamp);

    List<AircraftPosition> retrieveCurrentAircraftPosition(String tailNumber);

    List<AircraftPosition> retrieveCurrentPositionOfAllAircraft();

    /**
     * This method will do:
     *  1. Identify flights which are latest flights
     *  2. On each new flights, get tracking position
     * Notice: It will be invoked as schedule task
     * @param icao24
     */
    void updateAircraftPositionFromFlight(String icao24);
}
