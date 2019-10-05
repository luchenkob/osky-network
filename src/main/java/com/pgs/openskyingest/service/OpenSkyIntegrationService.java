package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftOpenskyInfo;
import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;
import java.util.Map;

public interface OpenSkyIntegrationService {
    List<AircraftOpenskyInfo> getIcao24FromTailNumber(String tailNumber);

    AircraftMetadata getMetadataOfAircraft(String icao24);

    List<AircraftPosition> getAllStateVectorOfMultiAircraft(Map<String, String> icao24WithRegistrations);

    /**
     * GET /states/all
     * @return
     */
    List<AircraftPosition> getAllCurrentStateVector();

    /**
     * GET /flights/aircraft
     * The given time interval must not be larger than 30 days!
     * @param icao24 Unique ICAO 24-bit address of the transponder in hex string representation.
     *      *        All letters need to be lower case
     * @param begin Start of time interval to retrieve flights for as Unix time (seconds since epoch)
     * @param end End of time interval to retrieve flights for as Unix time (seconds since epoch)
     * @return List of Aircraft Flights
     */
    List<AircraftFlight> getFlightsOfAircraft(String icao24, Long begin, Long end);

    /**
     * GET /tracks
     * @param icao24 Unique ICAO 24-bit address of the transponder in hex string representation.
     *               All letters need to be lower case
     * @param time Unix time in seconds since epoch. It can be any time betwee start and end of a known flight.
     *             If time = 0, get the live track if there is any flight ongoing for the given aircraft.
     * @return List of Aircraft Postion
     */
    List<AircraftPosition> getTrackedPositionOfAircraft(String icao24, Long time);

}
