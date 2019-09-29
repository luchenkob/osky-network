package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;

import java.util.List;

public interface AircraftPositionAggregationRepository {

    List<AircraftPosition> findLastestPositionOfAllAircraft();

}
