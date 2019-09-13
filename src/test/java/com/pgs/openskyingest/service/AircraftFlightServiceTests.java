//package com.pgs.openskyingest.service;
//
//import com.pgs.openskyingest.model.AircraftFlight;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class AircraftFlightServiceTests {
//
//    @Autowired
//    private AircraftFlightService aircraftFlightService;
//
//    @Test
//    public void gettingAndUpdateStateOfAllWatchingAircraft() {
//        Map<String, List<AircraftFlight>> ret = aircraftFlightService.retrieveAircraftFlightGroupByDate("N15GX", 1565715600l, 1568307600l);
//        System.out.print(ret.size());
//    }
//}
