package com.pgs.openskyingest.service.impl;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftFlightCompare;
import com.pgs.openskyingest.model.AirportMetadata;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.service.AircraftFlightService;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.AirportMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AircraftFlightServiceImpl implements AircraftFlightService {

    private final static Logger logger = LoggerFactory.getLogger(AircraftFlightServiceImpl.class);

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Autowired
    private AircraftMetadataService aircraftMetadataService;

    @Autowired
    private AirportMetadataService airportMetadataService;

    @Override
    public List<AircraftFlight> retrieveAircraftFlightInTime(String tailNumberWithIcao24, Long fromTimestamp, Long toTimestamp) {
        // parse tailNumberWithIcao24 to get tailNumber vs icao24
        Pattern pattern = Pattern.compile("(.*)\\((.*)\\)");
        Matcher matcher = pattern.matcher(tailNumberWithIcao24);

        String tailNumber = "";
        String icao24 = "";
        while (matcher.find()) {
            tailNumber = matcher.group(1);
            icao24 = matcher.group(2).toLowerCase();
        }

        logger.info("Retrieve aircraft flight of icao24 {} tailNumber {} in time [{}, {}]", icao24, tailNumber, fromTimestamp, toTimestamp);

        List<AircraftFlight> flights = aircraftFlightRepository.findAircraftFlightByIcao24EqualsAndFirstSeenBetween(icao24, fromTimestamp, toTimestamp);
        fillTailNumberAndAirportAndOwner(flights, tailNumber, icao24);

        return flights;
    }

    @Override
    public List<AircraftFlight> retrieveAllFlightsDepartureAt(String gpsCode) {
        List<AircraftFlight> aircraftFlights = aircraftFlightRepository.findAircraftFlightByEstDepartureAirportAndCallsignStartingWith(gpsCode, "N");
        fillTailNumberAndAirportAndOwner(aircraftFlights);
        return aircraftFlights;
    }

    @Override
    public List<AircraftFlight> retrieveAllFlightsArriveTo(String gpsCode) {
        List<AircraftFlight> aircraftFlights = aircraftFlightRepository.findAircraftFlightByEstArrivalAirportAndCallsignStartingWith(gpsCode, "N");
        fillTailNumberAndAirportAndOwner(aircraftFlights);
        return aircraftFlights;
    }

    @Override
    public List<AircraftFlight> retrieveAircraftFlightByIcao24EqualsAndFirstSeenBetween(String icao24, Long begin, Long end) {
        List<AircraftFlight> aircraftFlights = aircraftFlightRepository.findAircraftFlightByIcao24EqualsAndFirstSeenBetween(icao24, begin, end);
        fillTailNumberAndAirportAndOwner(aircraftFlights);
        return aircraftFlights;
    }

    @Override
    public AircraftFlight retrieveAircraftFlightByIcao24AndFirstSeenLessThanEqualAndLastSeenGreaterThanEqual(String icao24, Long from, Long to) {
        return aircraftFlightRepository.findAircraftFlightByIcao24AndFirstSeenLessThanEqualAndLastSeenGreaterThanEqual(icao24, from, to);
    }

    @Override
    public Map<String, List<AircraftFlight>> retrieveAircraftFlightGroupByDate(String tailNumberWithIcao24, Long fromTimestamp, Long toTimestamp, String clientTz) {
        Map<String, List<AircraftFlight>> retData = new LinkedHashMap<>();
        List<AircraftFlight> aircraftFlights = retrieveAircraftFlightInTime(tailNumberWithIcao24, fromTimestamp, toTimestamp);

        for (AircraftFlight flight : aircraftFlights) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone(clientTz));

            String date = sdf.format(new Date(flight.getFirstSeen() * 1000));

            if (retData.get(date) == null) {
                List<AircraftFlight> ls = new ArrayList<>();
                ls.add(flight);
                retData.put(date, ls);
            } else {
                retData.get(date).add(flight);
            }
        }

        return retData;
    }

    @Override
    public Map<String, List<AircraftFlightCompare>> retrieveAircraftsFlightGroupByDate(String[] tailNumberWithIcao24s, Long from, Long to, String clientTz) {
        Map<String, List<AircraftFlightCompare>> retData = new LinkedHashMap<>();

        // fill data
        for (String tailNumberWithIcao24 : tailNumberWithIcao24s) {
            Map<String, List<AircraftFlight>> flightGroupByDate = retrieveAircraftFlightGroupByDate(tailNumberWithIcao24, from, to, clientTz);

            for (String date : flightGroupByDate.keySet()) {
                List<AircraftFlight> flights = flightGroupByDate.get(date);

                AircraftFlightCompare aircraftFlightCompare = new AircraftFlightCompare(flights.get(0).getTailNumber());

                aircraftFlightCompare.setDeparture(flights.stream().map(f -> String.valueOf(f.getFirstSeen())).collect(Collectors.joining(",")));

                aircraftFlightCompare.setDepartureAirport(flights.stream().map(f -> {
                    String gpsCode = f.getEstDepartureAirport();
                    List<AirportMetadata> airportMetadatas = airportMetadataService.retrieveAirportMetadataByGpsCode(gpsCode);
                    return airportMetadatas.isEmpty() ? gpsCode : airportMetadatas.get(0).getName();
                }).collect(Collectors.joining("###")));

                aircraftFlightCompare.setArrival(flights.stream().map(f -> String.valueOf(f.getLastSeen())).collect(Collectors.joining(",")));

                aircraftFlightCompare.setArrivalAirport(flights.stream().map(f -> {
                    String gpsCode = f.getEstArrivalAirport();
                    List<AirportMetadata> airportMetadatas = airportMetadataService.retrieveAirportMetadataByGpsCode(gpsCode);
                    return airportMetadatas.isEmpty() ? gpsCode : airportMetadatas.get(0).getName();
                }).collect(Collectors.joining("###")));

                aircraftFlightCompare.setIcao24(flights.stream().map(f -> String.valueOf(f.getIcao24())).collect(Collectors.joining(",")));

                if (retData.get(date) == null) {
                    List<AircraftFlightCompare> afcls = new ArrayList<>();
                    afcls.add(aircraftFlightCompare);
                    retData.put(date, afcls);
                } else {
                    retData.get(date).add(aircraftFlightCompare);
                }
            }
        }

        // fill missing data
        for (Map.Entry<String, List<AircraftFlightCompare>> entry : retData.entrySet()) {
            List<AircraftFlightCompare> newList = new ArrayList<>();
            List<AircraftFlightCompare> currentList = entry.getValue();

            for (String tailNumberWithIcao24 : tailNumberWithIcao24s) {
                newList.add(currentList.stream()
                        .filter(afc -> afc.getTailNumber().equalsIgnoreCase(tailNumberWithIcao24))
                        .findFirst()
                        .orElse(new AircraftFlightCompare(tailNumberWithIcao24)));
            }
            //update entry
            entry.setValue(newList);
        }

        // return sorted key
        return retData.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Long deleteAircraftFlightByIcao24(String icao24) {
        return aircraftFlightRepository.deleteAircraftFlightByIcao24(icao24);
    }

    @Override
    public Long numberOfRecords() {
        return aircraftFlightRepository.count();
    }

    @Override
    public List<AircraftFlight> insertAll(List<AircraftFlight> aircraftFlights) {
        return aircraftFlightRepository.saveAll(aircraftFlights);
    }

    @Override
    @Cacheable("isFlightExist")
    public Boolean isFlightExist(AircraftFlight aircraftFlight) {
        return aircraftFlightRepository.existsAircraftFlightByIcao24EqualsAndFirstSeenEqualsAndLastSeenEquals(aircraftFlight.getIcao24(), aircraftFlight.getFirstSeen(), aircraftFlight.getLastSeen());
    }

    private void fillTailNumberAndAirportAndOwner(List<AircraftFlight> aircraftFlights) {
        fillTailNumberAndAirportAndOwner(aircraftFlights, "", "");
    }

    private void fillTailNumberAndAirportAndOwner(List<AircraftFlight> aircraftFlights, String tailNumber, String icao24) {
        aircraftFlights.parallelStream().forEach(flight -> {
            if (!tailNumber.isEmpty() && !icao24.isEmpty()) {
                flight.setTailNumber(tailNumber + "(" + icao24 + ")");
            }
            flight.setEstDepartureAirport(getAiportName(flight.getEstDepartureAirport()));
            flight.setEstArrivalAirport(getAiportName(flight.getEstArrivalAirport()));
            flight.setOwner(aircraftMetadataService.getAircraftOwner(flight.getIcao24()));
        });
    }

    private String getAiportName(String gpsCode) {
        List<AirportMetadata> airports = airportMetadataService.retrieveAirportMetadataByGpsCode(gpsCode);
        if (!airports.isEmpty()) {
            return airports.get(0).getName();
        } else {
            return gpsCode;
        }
    }

}
