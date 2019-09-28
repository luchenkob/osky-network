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

@CrossOrigin(origins = {"http://localhost:3000", "https://opensky-ingest-fe.herokuapp.com"})
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

    // TODO: we will change it back to icao24 again
    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.POST)
    public AircraftMetadata addAircraftForWatching(@RequestBody String tailNumber) {
        tailNumber = tailNumber.toUpperCase();
        int result = configManagmentService.insertWatchingAircaftConfig(tailNumber);
        if (result > 0) {
            return configManagmentService.retrieveAircraftMetadataByRegistration(tailNumber).get(0);
        }
        return null;
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
        String[] jsonRets = configManagmentService.retrieveAllAircraftTailNumber();
        ObjectMapper objectMapper = new ObjectMapper();
        for (String json : jsonRets) {
            try {
                String tailNumber = objectMapper.readTree(json).get("registration").textValue();
                ret.add(tailNumber);
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
