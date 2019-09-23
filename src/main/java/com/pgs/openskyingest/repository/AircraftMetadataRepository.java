package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AircraftMetadataRepository extends MongoRepository<AircraftMetadata, String> {
    List<AircraftMetadata> findAircraftMetadataByIsTracking(boolean isTracking);

    AircraftMetadata findAircraftMetadataByIcao24(String icao24);

    AircraftMetadata findAircraftMetadataByRegistration(String registration);

    List<AircraftMetadata> findAircraftMetadataByRegistrationContains(String registration);

    @Query(value="{}",fields="{ '_id': 0, 'registration' : 1}")
    String[] findAllAircraftTailNumber();

    @Query(value="{}",fields="{ '_id': 0, 'icao24' : 1}")
    List<String> findAllAircraftIcao24();
}
