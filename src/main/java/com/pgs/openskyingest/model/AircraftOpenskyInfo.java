package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AircraftOpenskyInfo {
    private String icao24;
    private String registration;
    private String model;
    private String operator;
    private String country;
}
