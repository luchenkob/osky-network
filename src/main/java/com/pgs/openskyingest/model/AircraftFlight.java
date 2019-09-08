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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AircraftFlight that = (AircraftFlight) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(icao24, that.icao24)
                .append(firstSeen, that.firstSeen)
                .append(estDepartureAirport, that.estDepartureAirport)
                .append(lastSeen, that.lastSeen)
                .append(estArrivalAirport, that.estArrivalAirport)
                .append(callsign, that.callsign)
                .append(estDepartureAirportHorizDistance, that.estDepartureAirportHorizDistance)
                .append(estDepartureAirportVertDistance, that.estDepartureAirportVertDistance)
                .append(estArrivalAirportHorizDistance, that.estArrivalAirportHorizDistance)
                .append(estArrivalAirportVertDistance, that.estArrivalAirportVertDistance)
                .append(departureAirportCandidatesCount, that.departureAirportCandidatesCount)
                .append(arrivalAirportCandidatesCount, that.arrivalAirportCandidatesCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(icao24)
                .append(firstSeen)
                .append(estDepartureAirport)
                .append(lastSeen)
                .append(estArrivalAirport)
                .append(callsign)
                .append(estDepartureAirportHorizDistance)
                .append(estDepartureAirportVertDistance)
                .append(estArrivalAirportHorizDistance)
                .append(estArrivalAirportVertDistance)
                .append(departureAirportCandidatesCount)
                .append(arrivalAirportCandidatesCount)
                .toHashCode();
    }
}
