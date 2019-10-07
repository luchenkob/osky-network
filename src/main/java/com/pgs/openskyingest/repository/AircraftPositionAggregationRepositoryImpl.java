package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

public class AircraftPositionAggregationRepositoryImpl implements AircraftPositionAggregationRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AircraftPositionAggregationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<AircraftPosition> findLastestPositionOfAllAircraft(Pageable pageable) {
        SortOperation sortOperation = Aggregation.sort(new Sort(Sort.Direction.DESC, "timePosition"));
        GroupOperation groupOperation = getGroupOperation();

        Aggregation aggregation = Aggregation.newAggregation(
                sortOperation
                , groupOperation
                , skip(pageable.getPageNumber() * pageable.getPageSize() * 1L)
                , limit(pageable.getPageSize())
                )
                .withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        AggregationResults<AircraftPosition> result = mongoTemplate.aggregate(aggregation, "aircraftPosition", AircraftPosition.class);
        return result.getMappedResults();
    }

    @Override
    public List<AircraftPosition> findLatestPositionOfAircraft(String icao24) {
        MatchOperation matchOperation = Aggregation.match(new Criteria("icao24").is(icao24));
        SortOperation sortOperation = Aggregation.sort(new Sort(Sort.Direction.DESC, "timePosition"));
        GroupOperation groupOperation = getGroupOperation();

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, groupOperation)
                .withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        AggregationResults<AircraftPosition> result = mongoTemplate.aggregate(aggregation, "aircraftPosition", AircraftPosition.class);
        return result.getMappedResults();
    }

    private GroupOperation getGroupOperation() {
        return Aggregation.group("icao24")
                .max("timePosition").as("maxTimePosition")
                .first("icao24").as("icao24")
                .first("latitude").as("latitude")
                .first("longitude").as("longitude")
                .first("baroAltitude").as("baroAltitude")
                .first("trueTrack").as("trueTrack")
                .first("onGround").as("onGround");

    }
}
