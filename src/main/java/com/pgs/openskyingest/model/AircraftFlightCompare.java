package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AircraftFlightCompare {
    private String departure;
    private String arrival;
    private String tailNumber;
    private String icao24;
}
