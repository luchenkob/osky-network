package com.pgs.openskyingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.repository.AircraftFlightRepository;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.repository.AircraftPositionRepository;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class UpdateFlightsOfWatchingAircraftsTask {
    private static final Logger logger = LoggerFactory.getLogger(UpdateFlightsOfWatchingAircraftsTask.class);

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Autowired
    private AircraftFlightRepository aircraftFlightRepository;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000) // 2hrs
    public void updateFlightsOfWatchingAircrafts() {
        List<String> jsonRets = aircraftMetadataRepository.findAllAircraftTailNumber();
        ObjectMapper objectMapper = new ObjectMapper();

        // identify latest flights
        Long end = Instant.now().getEpochSecond();
        Long begin = end - 30*24*60*60;

        logger.info("Trigger getting and updating flights between {} and {}", begin, end);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (String json : jsonRets) {
            executor.execute(() -> {
                try {
                    String icao24 = objectMapper.readTree(json).get("icao24").textValue();

                    List<AircraftFlight> flights = openSkyIntegrationService.getFlightsOfAircraft(icao24, begin, end);
                    logger.info("For icao24 {} opensky return {} flights", icao24, flights.size());

                    List<AircraftFlight> dbFlights = aircraftFlightRepository.findAircraftFlightByIcao24EqualsAndFirstSeenBetween(icao24, begin, end);
                    logger.info("For icao24 {} in database return {} flights ", icao24, dbFlights.size());

                    List<AircraftFlight> newFlights = flights.stream().filter(flight -> !dbFlights.contains(flight)).collect(Collectors.toList());

                    if (!newFlights.isEmpty()) {
                        logger.info("For icao24 {}, found and saved {} new flights", icao24, newFlights.size());
                        aircraftFlightRepository.saveAll(newFlights);
                    }
                } catch (Exception e) {
                    // do nothing
                }
            });
        }

        executor.shutdown();
        while(!executor.isTerminated()) {
            //waiting threads finish running
        }
    }

    // TODO: we also need fill in missing fields of aircraft metadata
}