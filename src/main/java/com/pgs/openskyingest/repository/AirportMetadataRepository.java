package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AirportMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AirportMetadataRepository extends MongoRepository<AirportMetadata, String> {
    List<AirportMetadata> findAirportMetadataByGpsCode(String gpsCode);
    List<AirportMetadata> findAirportMetadataByNameContains(String query);
}
