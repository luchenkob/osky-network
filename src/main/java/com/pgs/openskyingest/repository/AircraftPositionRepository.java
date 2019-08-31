package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AircraftPositionRepository extends MongoRepository<AircraftPosition, String> {
    // Wil add more functions to query database
}
