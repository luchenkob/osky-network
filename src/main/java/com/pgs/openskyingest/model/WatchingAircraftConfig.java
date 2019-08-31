package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class WatchingAircraftConfig {

    @Id
    private String id;

    private String icao24;
    private String tialNumber;
    private AircraftMetadata aircraftMetadata;

}
