#Smart Counter
#### Assumptions
- We don't know anything about exact location of the Counter, and we find out by its Geo Location on first record
- Location of Counter is static, it means, Geo Location is unable to change
- There are lots of assumptions about consumption price, you can find it out by reading zone.json file

#### Note
- Id in all models are String and following scheme is not acceptable, it is swagger fault and resolved soon
```
{
    date	string($date-time)
    timestamp	integer($int32)
}
```
use **String** instead of above object.

- You can find structure of the system in System.drawio file
- Application isn't completed yet, It's just a MVP
- To run application you should have MongoDB and Redis on their default port
- The application is based on Hexagonal architecture, but it's combined with Spring structure and unfortunately make it 
a little confusing
- Implementation of **Port**s are shallow and not accurate at all

#### How To Run
- Make sure about Redis and MongoDB
- type following command in terminal
```
./mvnw spring-boot:run
```