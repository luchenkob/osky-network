package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AircraftPositionRepository extends MongoRepository<AircraftPosition, String> {
    List<AircraftPosition> findAircraftPositionsByIcao24EqualsAndTimePositionBetween(String icao24, Long fromTimestamp, Long toTimestamp);
}
