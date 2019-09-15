package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.service.ConfigManagmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com" })
@RestController
public class AircraftMetadataController {

    @Autowired
    private ConfigManagmentService configManagmentService;

    @RequestMapping(value = "/aircraft/metadata/all", method = RequestMethod.GET)
    public List<AircraftMetadata> getAllAircraft(@RequestParam(value="icao24", defaultValue = "") String icao24) {
        if (StringUtils.isEmpty(icao24)) {
            return configManagmentService.retrieveAllAircraft();
        } else {
            return Arrays.asList(configManagmentService.retrieveAircraftMetadataByIcao24(icao24));
        }
    }

    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.POST)
    public AircraftMetadata addAircraftForWatching(@RequestBody String tailNumber) {
        int result = configManagmentService.insertWatchingAircaftConfig(tailNumber);
        if (result > 0) {
            return configManagmentService.retrieveAircraftMetadataByRegistration(tailNumber);
        }
        return null;
    }

    @RequestMapping(value = "/aircraft/metadata/tailNumbers", method = RequestMethod.GET)
    public List<String> getAllAircraftTailNumber() {
        return configManagmentService.retrieveAllAircraftTailNumber();
    }

}
