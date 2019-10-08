package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface AircraftPositionService {

    List<AircraftPosition> retrieveAircraftPositionInTime(String tailNumberWithIcao24, Long fromTimestamp, Long toTimestamp);

    List<AircraftPosition> retrieveCurrentPositionOfAllAircraft();

    List<AircraftPosition> retrieveLatestPositionOfAllAircraft(int page, int size);

    List<AircraftPosition> retrieveLatestPositionOfAircraft(String tailNumberWithIcao24);

    Long deleteAircraftPositionByIcao24(String icao24);

    Long numberOfRecords();

    Integer numberOfLatestPositionRecords();

    List<AircraftPosition> insertAll(List<AircraftPosition> aircraftPositions);
}
