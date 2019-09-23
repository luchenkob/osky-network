//package com.pgs.openskyingest.service;
//
//import com.pgs.openskyingest.model.AircraftMetadata;
//import com.pgs.openskyingest.repository.AircraftMetadataRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class ConfigManagementServiceTests {
//
//    @Autowired
//    private ConfigManagmentService configManagmentService;
//
//    @Autowired
//    private AircraftMetadataRepository aircraftMetadataRepository;
//
//    @Test
//    public void addConfig() {
//        aircraftMetadataRepository.deleteAll();
//        configManagmentService.insertWatchingAircaftConfig("N15GX", "N17GX", "N171CL", "N4281K", "N817GS"
//                , "N636MF", "N838MF", "N607CH", "N273JC", "N10XG", "N904GG", "N2767F", "N28MS", "N194WM", "N887WM", "N543HA"
//                , "N628BD", "N931FL", "N456GA", "N451GV", "N673PH", "N2341B", "N23453", "N874CD", "N770LM", "N730LM");
//
//        for (AircraftMetadata meta : aircraftMetadataRepository.findAll()) {
//            System.out.println(meta);
//        }
//        //aircraftMetadataRepository.deleteAll();
//    }
////
////    @Test
////    public void findAllIsTrackingAircraft() {
////        configManagmentService.insertWatchingAircaftConfig("N171CL", "N4281K");
////        aircraftMetadataRepository.findAircraftMetadataByIsTracking(Boolean.TRUE).forEach(meta -> System.out.println(meta.toString()));
////    }
//}
//
