//package com.pgs.openskyingest.service;
//
//import com.pgs.openskyingest.model.AircraftFlight;
//import com.pgs.openskyingest.model.AircraftMetadata;
//import com.pgs.openskyingest.model.AircraftPosition;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.time.Instant;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class OpenSkyeIntegrationServiceTests {
//
//    @Autowired
//    private OpenSkyIntegrationService openSkyIntegrationService;
//
//    @Test
//    public void getIcao() {
//        String icao24 = openSkyIntegrationService.getIcao24FromTailNumber("N15GX");
//        Assert.assertEquals("a0c882", icao24);
//    }
//
//    @Test
//    public void getAircraftMetadata() {
//        AircraftMetadata aircraftMetadata = openSkyIntegrationService.getMetadataOfAircraft("a0c882");
//        Assert.assertEquals("a0c882", aircraftMetadata.getIcao24());
//    }
//
//    @Test
//    public void getAllStateVectorOfAircraft() {
//        List<AircraftPosition> aircraftPositions = openSkyIntegrationService.getAllStateVectorOfAircraft("a11780", 1565656614l);
//        Assert.assertEquals(1, aircraftPositions.size());
//
//        aircraftPositions = openSkyIntegrationService.getAllStateVectorOfAircraft("a11780", Instant.now().getEpochSecond());
//        Assert.assertEquals(0, aircraftPositions.size());
//    }
//
//    @Test
//    public void getAllFlightOfAircraft() {
//        List<AircraftFlight> aircraftFlights = openSkyIntegrationService.getFlightsOfAircraft("a11780", 1564498800l, 1567004121l);
//        Assert.assertEquals(true, aircraftFlights != null && !aircraftFlights.isEmpty());
//    }
//
//    @Test
//    public void getTrackedPositionOfAircraft() {
//        List<AircraftPosition> positions = openSkyIntegrationService.getTrackedPositionOfAircraft("a11780", 1565656615l);
//        Assert.assertEquals(130, positions.size());
//        System.out.println(positions);
//    }
//}
