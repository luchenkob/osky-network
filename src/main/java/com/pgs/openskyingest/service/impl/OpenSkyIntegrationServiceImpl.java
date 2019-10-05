package com.pgs.openskyingest.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.openskyingest.model.AircraftFlight;
import com.pgs.openskyingest.model.AircraftMetadata;
import com.pgs.openskyingest.model.AircraftOpenskyInfo;
import com.pgs.openskyingest.model.AircraftPosition;
import com.pgs.openskyingest.repository.AircraftMetadataRepository;
import com.pgs.openskyingest.service.OpenSkyIntegrationService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OpenSkyIntegrationServiceImpl implements OpenSkyIntegrationService {

    private final Logger logger = LoggerFactory.getLogger(OpenSkyIntegrationServiceImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AircraftMetadataRepository aircraftMetadataRepository;

    @Override
    public List<AircraftOpenskyInfo> getIcao24FromTailNumber(String tailNumber) {
        List<AircraftOpenskyInfo> icao24s = new ArrayList<>();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://opensky-network.org/api/metadata/aircraft/list?n=50&p=1&q=" + tailNumber + "&sc=&sd=");

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            logger.info("getIcao24FromTailNumber json response {}", json);

            JsonNode root = objectMapper.readTree(json);
            icao24s = objectMapper.readValue(root.path("content").toString(), List.class);

            response.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return icao24s;
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
    public List<AircraftPosition> getAllStateVectorOfMultiAircraft(Map<String, String> icao24WithRegistrations) {
        CloseableHttpClient client = HttpClients.createDefault();
        StringBuilder url = new StringBuilder("https://tinnt:dnMdFfeKDcf9vQ!@opensky-network.org/api/states/all?");

        Set<String> icao24s = icao24WithRegistrations.keySet();
        icao24s.forEach(icao24 -> {
            url.append("icao24=");
            url.append(icao24);
            url.append("&");
        });

        String urlStr = url.toString();

        HttpGet httpGet = new HttpGet(urlStr.substring(0, urlStr.length() - 1));

        return executeGetAndExtractAircraftPosition(client, httpGet);
    }

    @Override
    public List<AircraftPosition> getAllCurrentStateVector() {
        CloseableHttpClient client = HttpClients.createDefault();
        StringBuilder url = new StringBuilder("https://tinnt:dnMdFfeKDcf9vQ!@opensky-network.org/api/states/all");
        HttpGet httpGet = new HttpGet(url.toString());
        return executeGetAndExtractAircraftPosition(client, httpGet);
    }

    @Override
    public List<AircraftFlight> getFlightsOfAircraft(String icao24, Long begin, Long end) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://tinnt:dnMdFfeKDcf9vQ!@opensky-network.org/api/flights/aircraft?icao24="
                + icao24
                + "&begin=" + begin
                + "&end=" + end);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            logger.info("Returned flights of aircraft {} between {} and {} is: {}", icao24, begin, end, json);
            return objectMapper.readValue(json, new TypeReference<List<AircraftFlight>>(){});

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<AircraftPosition> getTrackedPositionOfAircraft(String icao24, Long time) {
        CloseableHttpClient client = HttpClients.createDefault();
        logger.info("Get tracked positions of aircraft {} at time {}", icao24, time);
        HttpGet httpGet = new HttpGet("https://tinnt:dnMdFfeKDcf9vQ!@opensky-network.org/api/tracks/all?icao24=" + icao24 + "&time=" + time);

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            JsonNode paths = objectMapper.readTree(json).path("path");
            List<AircraftPosition> aircraftPositions = new ArrayList<>();
            if (paths.isArray()) {
                for (final JsonNode path : paths) {
                    AircraftPosition aircraftPosition = new AircraftPosition();
                    aircraftPosition.setIcao24(icao24);
                    aircraftPosition.setTimePosition(path.path(0).asLong());
                    aircraftPosition.setLatitude(path.path(1).asDouble());
                    aircraftPosition.setLongitude(path.path(2).asDouble());
                    aircraftPosition.setBaroAltitude(path.path(3).asDouble());
                    aircraftPosition.setTrueTrack(path.path(4).asDouble());
                    aircraftPosition.setOnGround(path.path(5).asBoolean());

                    aircraftPositions.add(aircraftPosition);
                }
            }

            return aircraftPositions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<AircraftPosition> executeGetAndExtractAircraftPosition(CloseableHttpClient client, HttpGet httpGet) {
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String json = EntityUtils.toString(response.getEntity());
            logger.info("Returned state vectors of aircraft at {}: {}", httpGet.toString(), json);
            JsonNode states = objectMapper.readTree(json).path("states");
            List<AircraftPosition> aircraftPositions = new ArrayList<>();

            if (states.isArray()) {
                for (final JsonNode state : states) {
                    AircraftPosition aircraftPosition = new AircraftPosition();
                    aircraftPosition.setIcao24(state.path(0).asText());
                    aircraftPosition.setTailNumber(state.path(1).asText());   // assume callsign is tailNumber
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
