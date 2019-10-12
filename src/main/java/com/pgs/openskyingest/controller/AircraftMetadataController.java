package com.pgs.openskyingest.controller;

import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftOpenskyInfo;
import com.pgs.openskyingest.response.ResponseGeneric;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import com.pgs.openskyingest.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AircraftMetadataService aircraftMetadataService;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @RequestMapping(value = "/aircraft/metadata/all", method = RequestMethod.GET)
    public List<AircraftMetadata> getAllAircraft(@RequestParam(value = "tailNumber", defaultValue = "") String tailNumbers,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        if (tailNumbers.isEmpty()) {
            return aircraftMetadataService.retrieveAllAircraft(page, size);
        } else {
            List<AircraftMetadata> aircrafts = new ArrayList<>();
            String[] tailNumbersArray = tailNumbers.split(",");
            for (String tailNumberWithIcao24 : tailNumbersArray) {
                String icao24 = Utils.extractIcao24(tailNumberWithIcao24);
                aircrafts.add(aircraftMetadataService.retrieveAircraftMetadataByIcao24(icao24));
            }
            return aircrafts;
        }
    }

    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.POST)
    public ResponseGeneric addAircraftForWatching(@RequestBody String icao24) {
        icao24 = icao24.toLowerCase();
        int result = aircraftMetadataService.insertWatchingAircaftConfig(icao24);
        if (result > 0) {
            return new ResponseGeneric(false, "Add aircraft successfully", aircraftMetadataService.retrieveAircraftMetadataByIcao24(icao24));
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
    public List<String> getAllAircraftTailNumber(@RequestParam(value = "q", defaultValue = "") String query,
                                                 @RequestParam(value = "page") int page,
                                                 @RequestParam(value = "size") int size) {
        List<String> ret = new ArrayList<>();
        List<AircraftMetadata> aircraftMetadataList = aircraftMetadataService.retrieveAircraftMetadataByRegistration(query, page, size);
        for (AircraftMetadata aircraftMetadata : aircraftMetadataList) {
            try {
                ret.add(aircraftMetadata.getRegistration() + "(" + aircraftMetadata.getIcao24() + ")");
            } catch (Exception e) {
                // do nothing
            }
        }

        return ret;
    }

    @RequestMapping(value = "/aircraft/metadata", method = RequestMethod.DELETE)
    public Long deleteTrackingAircraft(@RequestBody String icao24) {
        return aircraftMetadataService.deleteAircraft(icao24);
    }

}
