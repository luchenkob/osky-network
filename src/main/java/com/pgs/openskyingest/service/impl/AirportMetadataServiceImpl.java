package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AirportMetadata;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AirportMetadataRepository;
import com.pgs.openskyingest.service.AirportMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportMetadataServiceImpl implements AirportMetadataService {

    @Autowired
    private AirportMetadataRepository airportMetadataRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Override
    public List<AirportMetadata> retrieveAirportMetadata(String gpsCode) {
        return airportMetadataRepository.findAirportMetadataByGpsCode(gpsCode);
    }

    @Override
    public List<AirportMetadata> retrieveAirportMetadataByUserInput(String query) {
        return airportMetadataRepository.findAirportMetadataByNameContainsIgnoreCase(query);
    }

    @Override
    public List<AircraftFlight> retrieveAllFlightsDepartureAt(String gpsCode) {
        return aircraftFlightRepository.findAircraftFlightByEstDepartureAirport(gpsCode);
    }

    @Override
    public List<AircraftFlight> retrieveAllFlightsArriveTo(String gpsCode) {
        return aircraftFlightRepository.findAircraftFlightByEstArrivalAirport(gpsCode);
    }

    @Override
    public Long numberOfRecords() {
        return aircraftFlightRepository.count();
    }
}
