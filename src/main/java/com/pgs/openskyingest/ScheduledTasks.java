package com.pgs.openskyingest;

import com.pgs.openskyingest.service.AircraftStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
   private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

   @Autowired
   private AircraftStateService aircraftStateService;

    @Scheduled(fixedRate = 10000)
    public void updateStateOfWatchingAircrafts() {
        logger.info("Trigger getting and updating all watching aircraft");
        aircraftStateService.getCurrentStateVectorOfWatchingAircrafts();
    }
}
