package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "aircraftFlight")
public class AircraftFlight {
    private String icao24;
    private Long firstSeen;
    private String estDepartureAirport;
    private Long lastSeen;
    private String estArrivalAirport;
    private String callsign;
    private Integer estDepartureAirportHorizDistance;
    private Integer estDepartureAirportVertDistance;
    private Integer estArrivalAirportHorizDistance;
    private Integer estArrivalAirportVertDistance;
    private Integer departureAirportCandidatesCount;
    private Integer arrivalAirportCandidatesCount;
}
