package com.pgs.openskyingest.repository;

import com.pgs.openskyingest.model.AircraftFlight;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AircraftFlightRepository extends MongoRepository<AircraftFlight, String> {
    List<AircraftFlight> findAircraftFlightByIcao24(String icao24);

    List<AircraftFlight> findAircraftFlightByIcao24EqualsAndFirstSeenBetween(String icao24, Long fromTimestamp, Long toTimestamp);

    List<AircraftFlight> findAircraftFlightByEstDepartureAirportAndCallsignStartingWith(String gpsCode, String callSignPrefix);

    List<AircraftFlight> findAircraftFlightByEstArrivalAirportAndCallsignStartingWith(String gpsCode, String callSignPrefix);

    AircraftFlight findAircraftFlightByIcao24AndFirstSeenLessThanEqualAndLastSeenGreaterThanEqual(String icao24, Long fromTimestamp, Long toTimestamp);

    Boolean existsAircraftFlightByIcao24EqualsAndFirstSeenEqualsAndLastSeenEquals(String icao24, Long firstSeen, Long lastSeen);

    Long deleteAircraftFlightByIcao24(String icao24);
}
