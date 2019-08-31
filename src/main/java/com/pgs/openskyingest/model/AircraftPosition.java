package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class AircraftPosition {

    @Id
    public String id;

    public Integer time;
    public Float latitude;
    public Float longitude;
    public Float baroAltitude;
    public Float trueTrack;
    public Boolean onGround;

}
