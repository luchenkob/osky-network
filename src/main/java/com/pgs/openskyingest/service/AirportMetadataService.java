package com.pgs.openskyingest.service;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AirportMetadata;

import java.util.List;

public interface AirportMetadataService {

    List<AirportMetadata> retrieveAirportMetadata(String gpsCode);

    List<AircraftFlight> retrieveAllFlightsDepartureAt(String gpsCode);

    List<AircraftFlight> retrieveAllFlightsArriveTo(String gpsCode);
}
