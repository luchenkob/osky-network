package com.pgs.openskyingest.service;


import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AircraftStateServiceTests {
    @Autowired
    private AircraftStateService aircraftStateService;

    @Autowired
    private ConfigManagmentService configManagmentService;

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Test
    public void gettingAndUpdateStateOfAllWatchingAircraft() {
        aircraftMetadataRepository.deleteAll();
        configManagmentService.insertWatchingAircaftConfig("N15GX", "N17GX", "N171CL", "N4281K", "N817GS", "N636MF"
                , "N838MF", "N607CH", "N273JC", "N10XG", "N904G", "N2767", "N28MS", "N194WM", "N887WM", "N543H", "N628BD", "N931FL"
                , "N456GA", "N451GV", "N673P", "N508P", "N608wb", "N628bd", "N874C", "N674RW", "N502PC", "N1892", "N486rw", "N586rw"
                , "N286rw", "N386rw");
        aircraftStateService.getCurrentStateVectorOfWatchingAircrafts();
    }
}
