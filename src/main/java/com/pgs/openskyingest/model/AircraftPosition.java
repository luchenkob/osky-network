package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "aircraftPosition")
public class AircraftPosition {

    @Id
    private String id;

    private Integer time;
    private Float latitude;
    private Float longitude;
    private Float baroAltitude;
    private Float trueTrack;
    private Boolean onGround;

}
