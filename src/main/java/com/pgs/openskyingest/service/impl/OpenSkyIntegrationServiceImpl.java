package com.pgs.openskyingest.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.constant.Constant;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenSkyIntegrationServiceImpl implements OpenSkyIntegrationService {

    private final Logger logger = LoggerFactory.getLogger(OpenSkyIntegrationServiceImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getIcao24FromTailNumber(String tailNumber) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://opensky-network.org/api/metadata/aircraft/list?n=50&p=1&q=" + tailNumber + "&sc=&sd=");

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            logger.info("getIcao24FromTailNumber json response {}", json);

            JsonNode root = objectMapper.readTree(json);
            String icao24 = root.path("content").path(0).path("icao24").asText(Constant.ICAO24_NOT_FOUND);

            response.close();
            client.close();

            return icao24;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Constant.ICAO24_NOT_FOUND;
    }

    @Override
    public AircraftMetadata getMetadataOfAircraft(String icao24) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://opensky-network.org/api/metadata/aircraft/icao/" + icao24);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            AircraftMetadata aircraftMetadata = objectMapper.readValue(json, AircraftMetadata.class);
            logger.info("aircraft icao24 {} has metadata: {}", icao24, aircraftMetadata.toString());

            response.close();
            client.close();

            return aircraftMetadata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<AircraftPosition> getAllStateVectorOfAircraft(String icao24, Long timestamp) {
        logger.info("Getting all state vectors of aircraft {} at {}", icao24, timestamp);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://tinnt:dnMdFfeKDcf9vQ!@opensky-network.org/api/states/all?icao24=" + icao24 + "&time=" + timestamp);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonNode states = objectMapper.readTree(json).path("states");
            List<AircraftPosition> aircraftPositions = new ArrayList<>();

            if (states.isArray()) {
                for (final JsonNode state : states) {
                    AircraftPosition aircraftPosition = new AircraftPosition();
                    aircraftPosition.setIcao24(state.path(0).asText());
                    aircraftPosition.setTimePosition(state.path(3).asLong());
                    aircraftPosition.setLongitude(state.path(5).asDouble());
                    aircraftPosition.setLatitude(state.path(6).asDouble());
                    aircraftPosition.setBaroAltitude(state.path(7).asDouble());
                    aircraftPosition.setOnGround(state.path(8).asBoolean());
                    aircraftPosition.setVerticalRate(state.path(9).asDouble());
                    aircraftPosition.setTrueTrack(state.path(10).asDouble());

                    logger.info("Aircraft position obtain from state array: {}", aircraftPosition.toString());
                    aircraftPositions.add(aircraftPosition);
                }
            }

            response.close();
            client.close();

            return aircraftPositions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
