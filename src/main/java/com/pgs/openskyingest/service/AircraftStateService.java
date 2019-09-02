package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface AircraftStateService {
    /**
     * This method will be triggered as scheduled task, it will do:
     * 1. Get all aircrafts metadata which have isTracking flag = TRUE
     * 2. Invoke OpenSky API to get current state for each aircraft
     */
    void getCurrentStateVectorOfWatchingAircrafts();

    List<AircraftPosition> retrieveAircraftPositionInTime(String icao24, Long fromTimestamp, Long toTimestamp);
}
