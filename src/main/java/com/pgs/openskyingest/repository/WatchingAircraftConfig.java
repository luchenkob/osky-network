package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface  WatchingAircraftConfig extends MongoRepository<AircraftPosition, String> {
}
