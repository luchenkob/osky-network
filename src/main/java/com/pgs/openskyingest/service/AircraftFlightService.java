package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftFlight;

import java.util.List;

public interface AircraftFlightService {
    List<AircraftFlight> retrieveAircraftFlightInTime(String tailNumber, Long fromTimestamp, Long toTimestamp);
}
