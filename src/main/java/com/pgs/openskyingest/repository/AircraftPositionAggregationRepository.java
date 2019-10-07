package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AircraftPositionAggregationRepository {

    List<AircraftPosition> findLastestPositionOfAllAircraft(Pageable pageable);

    List<AircraftPosition> findLatestPositionOfAircraft(String icao24);

}
