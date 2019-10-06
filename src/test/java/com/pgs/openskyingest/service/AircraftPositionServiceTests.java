//package com.pgs.openskyingest.service;
//
//
//import com.pgs.openskyingest.model.AircraftPosition;
//import com.pgs.openskyingest.repository.AircraftMetadataRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class AircraftPositionServiceTests {
//    @Autowired
//    private AircraftPositionService aircraftPositionService;
//
//    @Autowired
//    private AircraftMetadataService configManagmentService;
//
//    @Autowired
//    private AircraftMetadataRepository aircraftMetadataRepository;
//
////    @Test
////    public void gettingAndUpdateStateOfAllWatchingAircraft() {
////        aircraftPositionService.updateAircraftPositionFromFlight("a78dbe");
////    }
//
//    @Test
//    public void gettingAndUpdateStateOfAllWatchingAircraft() {
//        List<AircraftPosition> list = aircraftPositionService.retrieveLatestPositionOfAllAircraft();
//        for (AircraftPosition aircraftPosition : list) {
//            System.out.println(aircraftPosition);
//        }
//    }
//}
