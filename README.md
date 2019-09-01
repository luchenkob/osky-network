# Getting Started

### To Track An Aircraft
Invoke OpenSky API, but time value should be the time the aircraft was flying  
`curl 'https://USERNAME:PASSWORD@opensky-network.org/api/tracks/all?icao24=a11780&time=1565656613'`

So, to know suitable value for time parameter, we should invoke finding flights of an aircraft by:  
`curl 'https://USERNAME:PASSWORD@opensky-network.org/api/flights/aircraft?icao24=a11780&begin=1564498800&end=1567004121'`  
Notice that: The given time interval must not be larger than 30 days!

### To Get All State Vectors Of An Aircraft
`https://USERNAME:PASSWORD@opensky-network.org/api/states/all?icao24=a0c882`


