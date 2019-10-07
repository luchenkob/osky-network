package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;

import java.util.List;
import java.util.Map;

public interface AircraftFlightService {
    List<AircraftFlight> retrieveAircraftFlightInTime(String tailNumber, Long fromTimestamp, Long toTimestamp);

    List<AircraftFlight> retrieveAllFlightsDepartureAt(String gpsCode);

    List<AircraftFlight> retrieveAllFlightsArriveTo(String gpsCode);

    List<AircraftFlight> retrieveAircraftFlightByIcao24EqualsAndFirstSeenBetween(String icao24, Long begin, Long end);

    AircraftFlight retrieveAircraftFlightByIcao24AndFirstSeenLessThanEqualAndLastSeenGreaterThanEqual(String icao24, Long from, Long to);

    Map<String, List<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumber, Long fromTimestamp, Long toTimestamp, String clientTz);

    Map<String, List<AircraftFlightCompare>> retrieveAircraftsFlightGroupByDate(String[] tailNumberWithIcao24s, Long from, Long to, String clientTz);

    Long deleteAircraftFlightByIcao24(String icao24);

    Long numberOfRecords();

    List<AircraftFlight> insertAll(List<AircraftFlight> aircraftFlights);
}
