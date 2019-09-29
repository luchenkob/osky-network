package com.pgs.openskyingest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "aircraftPosition")
public class AircraftPosition {

    @Id
    private String id;

    private String icao24;

    private Long timePosition;
    private Double latitude;
    private Double longitude;

    // Barometric altitude in meters. Can be null.
    private Double baroAltitude;

    // True track in decimal degrees clockwise from north (north=0°). Can be null.
    private Double trueTrack;

    // Vertical rate in m/s. A positive value indicates that the airplane is climbing,
    // a negative value indicates that it descends. Can be null.
    private Double verticalRate;

    // Boolean value which indicates if the position was retrieved from a surface position report.
    private Boolean onGround;

    private String tailNumber;
//    0  icao24	        "a11780",
//    1  callsign	        "N17GX ",
//    2  origin_country    "United States",
//    3  time_position	    1565656613,
//    4  last_contact	    1565656613,
//    5  longitude	        -121.9619,
//    6  latitude	        37.4423,
//    7  baro_altitude	    1082.04,
//    8  on_ground	        false,
//    9  velocity	        125.03,
//    10 true_track	     52.19,
//    11 vertical_rate	     7.8,
//    12 sensors	         null,
//    13 geo_altitude	     1112.52,
//    14 squawk	         "4511",
//    15 spi	              false,
//    16 position_source     0


}
