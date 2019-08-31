# Getting Started

### Solution
We will have 3 services: 
- Config Management Service will take care of insert/load config like add/delete tail number/registration aircraft
- OpenSky Integration Service will handle communicate between our application with OpenSky by invoking their API(s)
- Get Aircraft State Cron Service will be configured to run as schedule task

P/S: I've just have another idea, to make our application scalability we should use Queue like ActiveMQ or RaqbitMQ. The cron 
service just publish command to queue, and we will have many workers that handler getting aircraft state

