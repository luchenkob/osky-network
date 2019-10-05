package com.pgs.openskyingest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftOpenskyInfo;
import com.pgs.openskyingest.response.ResponseGeneric;
import com.pgs.openskyingest.service.ConfigManagmentService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://ui.opensky-ingest.xyz"})
@RestController
public class AircraftMetadataController {

    @Autowired
    private ConfigManagmentService configManagmentService;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @RequestMapping(value = "/aircraft/metadata/all", method = RequestMethod.GET)
    public List<AircraftMetadata> getAllAircraft(@RequestParam(value = "tailNumber", defaultValue = "") String tailNumber) {
        tailNumber = tailNumber.toUpperCase();
        if (StringUtils.isEmpty(tailNumber)) {
            return configManagmentService.retrieveAllAircraft();
        } else {
            return configManagmentService.retrieveAircraftMetadataByRegistration(tailNumber);
        }
    }

    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.POST)
    public ResponseGeneric addAircraftForWatching(@RequestBody String icao24) {
        icao24 = icao24.toLowerCase();
        int result = configManagmentService.insertWatchingAircaftConfig(icao24);
        if (result > 0) {
            return new ResponseGeneric(false, "Add aircraft successfully", configManagmentService.retrieveAircraftMetadataByIcao24(icao24));
        }
        return new ResponseGeneric(true, "The aircraft has already existed in our database", null);
    }

    @RequestMapping(value = "/aircraft/icao24", method = RequestMethod.GET)
    public ResponseGeneric getIcao24FromTailNumber(@RequestParam(value = "tailNumber", defaultValue = "") String tailNumber) {
        tailNumber = tailNumber.toUpperCase().trim();
        List<AircraftOpenskyInfo> icao24Info = openSkyIntegrationService.getIcao24FromTailNumber(tailNumber);
        return new ResponseGeneric(true, "Found " + icao24Info.size() + " aircraft info with tail number: " + tailNumber, icao24Info);
    }

    @RequestMapping(value = "/aircraft/metadata/tailNumbers", method = RequestMethod.GET)
    public List<String> getAllAircraftTailNumber() {
        List<String> ret = new ArrayList<>();
        List<String> jsonRets = configManagmentService.retrieveAllAircraftTailNumber();
        ObjectMapper objectMapper = new ObjectMapper();
        for (String json : jsonRets) {
            try {
                String tailNumber = objectMapper.readTree(json).get("registration").textValue();
                String icao24 = objectMapper.readTree(json).get("icao24").textValue();
                ret.add(tailNumber + "(" + icao24 + ")");
            } catch (Exception e) {
                // do nothing
            }
        }

        return ret;
    }

    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.DELETE)
    public Long deleteTrackingAircraft(@RequestBody String icao24) {
        return configManagmentService.deleteAircraft(icao24);
    }

}
