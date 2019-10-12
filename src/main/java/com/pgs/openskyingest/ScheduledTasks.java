package com.pgs.openskyingest;

import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.service.AircraftFlightService;
import com.pgs.openskyingest.service.AircraftMetadataService;
import com.pgs.openskyingest.service.AircraftPositionService;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private AircraftPositionService aircraftPositionService;

    @Autowired
    private AircraftFlightService aircraftFlightService;

    @Autowired
    private OpenSkyIntegrationService openSkyIntegrationService;

    @Autowired
    private AircraftMetadataService aircraftMetadataService;

    @Async
    @Scheduled(fixedRate = 48 * 60 * 60 * 1000) // 48hrs
    public void updateFlightsOfWatchingAircrafts() {
        List<AircraftMetadata> aircraftMetadataList = aircraftMetadataService.retrieveAll();

        // identify latest flights
        Long end = Instant.now().getEpochSecond();
        Long begin = end - 30*24*60*60;  // TODO: after system is stable, we should change 30 to 2 days

        logger.info("Trigger getting and updating flights between {} and {}", begin, end);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (AircraftMetadata aircraftMetadata : aircraftMetadataList) {
            executor.execute(() -> {
                try {
                    String icao24 = aircraftMetadata.getIcao24();

                    List<AircraftFlight> flights = openSkyIntegrationService.getFlightsOfAircraft(icao24, begin, end);
                    logger.info("For icao24 {} opensky return {} flights", icao24, flights.size());

                    if (!flights.isEmpty()) {
                        List<AircraftFlight> dbFlights = aircraftFlightService.retrieveAircraftFlightByIcao24EqualsAndFirstSeenBetween(icao24, begin, end);
                        logger.info("For icao24 {} in database return {} flights ", icao24, dbFlights.size());

                        // for optimization, below code will run
                        // when opensky return number of flights > number of flights in our databsae
                        if (flights.size() > dbFlights.size()) {
                            List<AircraftFlight> newFlights = flights.stream().filter(flight -> !dbFlights.contains(flight)).collect(Collectors.toList());

                            if (!newFlights.isEmpty()) {
                                logger.info("For icao24 {}, found and saved {} new flights", icao24, newFlights.size());
                                aircraftFlightService.insertAll(newFlights);

                                // It's too often, we couldn't get position based on flight.
                                // Because in get all current state vector, opensky return null value for lat/long field
                                newFlights.forEach(flight -> {
                                    // update position of flight
                                    List<AircraftPosition> positions = openSkyIntegrationService.getTrackedPositionOfAircraft(icao24, flight.getFirstSeen());
                                    aircraftPositionService.insertAll(positions);
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    // most of exceptions will be thrown when opensky return 503 service temporay unavailable
                    try  {
                        Thread.sleep(2 * 60 * 1000L);
                    } catch (InterruptedException e1){
                        // Do nothing..
                    }
                }
            });
        }

        executor.shutdown();
        while(!executor.isTerminated()) {
            //waiting threads finish running
        }
    }

    @Async
    @Scheduled(fixedRate = 2 * 60 * 1000)  // 2m
    public void updatePositionOfWatchingAircrafts() {
        logger.info("Trigger getting and updating all watching aircraft...");

        List<AircraftPosition> livingPositionOfAircrafts = openSkyIntegrationService.getAllCurrentStateVector();
        logger.info("opensky return {} records of all state vectors", livingPositionOfAircrafts.size());

        List<AircraftPosition> willInsertToMongo = Collections.synchronizedList( new ArrayList() );

        ExecutorService executor = Executors.newFixedThreadPool(6);
        for (AircraftPosition position : livingPositionOfAircrafts) {
            executor.execute(() -> {
                boolean icao24Existed = aircraftMetadataService.isIcao24Exist(position.getIcao24());
                if (icao24Existed) {
                    // update list
                    willInsertToMongo.add(position);

                    // update on aircraft db
                    AircraftMetadata aircraftMetadata = aircraftMetadataService.retrieveAircraftMetadataByIcao24(position.getIcao24());
                    aircraftMetadata.setTimePosition(position.getTimePosition());

                    aircraftMetadataService.save(aircraftMetadata);
                }
            });
        }
        executor.shutdown();
        while(!executor.isTerminated()) {
            //waiting threads finish running
        }

        if (!willInsertToMongo.isEmpty()) {
            logger.info("saving {} aircrafts positions", willInsertToMongo.size());
            aircraftPositionService.insertAll(willInsertToMongo);
        } else {
            logger.info("saving 0 aircrafts positions");
        }
    }

}
