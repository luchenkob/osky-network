package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenSkyeIntegrationServiceTests {

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Test
    public void getIcao()  {
        String icao24 = openSkyIntegrationService.getIcao24FromTailNumber("N15GX");
        Assert.assertEquals("a0c882", icao24);
    }

    @Test
    public void getAircraftMetadata() {
        AircraftMetadata aircraftMetadata = openSkyIntegrationService.getMetadataOfAirCraft("a0c882");
        Assert.assertEquals("a0c882", aircraftMetadata.getIcao24());
    }
}
