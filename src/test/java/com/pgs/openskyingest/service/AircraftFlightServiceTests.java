//package com.pgs.openskyingest.service;
//
//import com.pgs.openskyingest.model.AircraftFlight;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class AircraftFlightServiceTests {
//
//    @Autowired
//    private AircraftFlightService aircraftFlightService;
//
//    @Test
//    public void testFlightExist() {
//        AircraftFlight aircraftFlight = new AircraftFlight();
//        aircraftFlight.setIcao24("a6a6b4");
//        aircraftFlight.setFirstSeen(1568894857L);
//        aircraftFlight.setLastSeen(1568900062L);
//        Assert.assertEquals(Boolean.TRUE, aircraftFlightService.isFlightExist(aircraftFlight));
//    }
//}
