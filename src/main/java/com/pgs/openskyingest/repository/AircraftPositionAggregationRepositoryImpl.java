package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;

import java.util.List;

public class AircraftPositionAggregationRepositoryImpl implements AircraftPositionAggregationRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AircraftPositionAggregationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AircraftPosition> findLastestPositionOfAllAircraft() {

        SortOperation sortOperation = Aggregation.sort(new Sort(Sort.Direction.DESC, "timePosition"));

        GroupOperation groupOperation = Aggregation.group("icao24")
                .max("timePosition").as("timePosition")
                .first("icao24").as("icao24")
                .first("latitude").as("latitude")
                .first("longitude").as("longitude")
                .first("baroAltitude").as("baroAltitude")
                .first("trueTrack").as("trueTrack")
                .first("onGround").as("onGround");

        Aggregation aggregation = Aggregation.newAggregation(sortOperation, groupOperation);

        AggregationResults<AircraftPosition> result = mongoTemplate.aggregate(aggregation, "aircraftPosition", AircraftPosition.class);
        return result.getMappedResults();
    }
}
