# OSKY-NETWORK

## How to install: 
1. To make application run, we have to install mongodb first. I assume your MongoDB will be in the same machine 
with this application and run at port 27017
2. Install maven.
2. Clone source code to your PC
3. Run cmd `mvn clean install`
4. Move to target folder, then run `java -jar target/opensky-ingest-0.0.1-SNAPSHOT.jar`

The application will be run at port 8080.

## How to use it: 
### To view all aircraft metadata inside our database: 
GET to `http://localhost:8080/aircraft/metadata/all`  
If you wanna to find information of specified icao24, just do `http://localhost:8080/aircraft/metadata/all?icao24=<icao24>`. 
Example `http://localhost:8080/aircraft/metadata/all?icao24=ace9e8`

### To create new aircraft need to be tracking: 
You can use postman or curl to post tail number to http://localhost:8080/aircraft/metadata  
Example with curl:  
```
curl -X POST \
  http://localhost:8080/aircraft/metadata \
  -H 'Content-Type: text/plain' \
  -d N931FL
```  
After you do it and received aircraft metadata info in json format, it mean that the aircraft will be watching 
by our application.

### To get aircraft position in specified time range
Just perform GET to this API: `http://localhost:8080/aircraft/<aircraft_icao24>/position?from=<timestamp>&to=<timestamp>`  

Example:  
`http://localhost:8080/aircraft/a11780/position?from=1567416398&to=1567410398`

