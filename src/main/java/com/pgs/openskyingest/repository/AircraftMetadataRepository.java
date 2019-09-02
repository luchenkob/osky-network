package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AircraftMetadataRepository extends MongoRepository<AircraftMetadata, String> {
    List<AircraftMetadata> findAircraftMetadataByIsTracking(boolean isTracking);
    AircraftMetadata findAircraftMetadataByIcao24(String icao24);
    AircraftMetadata findAircraftMetadataByRegistration(String registration);
}
