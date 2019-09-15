package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;

import java.util.List;
import java.util.Map;

public interface AircraftFlightService {
    List<AircraftFlight> retrieveAircraftFlightInTime(String tailNumber, Long fromTimestamp, Long toTimestamp);
    Map<String, List<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumber, Long fromTimestamp, Long toTimestamp, String clientTz);
    Map<String, List<AircraftFlightCompare>> retrieveAircraftsFlightGroupByDate(String[] tailNumbers, Long from, Long to, String clientTz);
}
