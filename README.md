# Reservly
Reservly is a tool for reserving game match queue

## Backend technology stack
* Scala
* Play Framework 2
* Slick
* PostgreSQL
* Swagger
* Websockets (Akka actors)
* Asana (Workflow planning)

## Frontend
Frontend repository available [here](https://github.com/mtrybus2208/game-reservation-app) 

## How to run
#### IntelliJ
* Install JDK11
* Install scala support for IntelliJ
* Import project to IntelliJ
* In IntelliJ: Edit Configurations -> Add New Configuration -> Play
* Use default config (with http://localhost:9000)
* Run project with created config

## Swagger docs
Swagger docs available at `localhost:9000/docs/`

## Models

### Player
```javascript
{ 
  "id": "1"                     //[Long: autogenerated]
  "firstName": "John"           //[String: size -> 2 to 60 letters]
  "lastName": "Smith"           //[String: size -> 2 to 60 letters]
}
```

### Match
```javascript
{ 
  "id": "1"                                   //[Long: autogenerated]
  "startDate": "2018-01-01T13:10:27.00Z"      //[java.sql.Timestamp: start date must be before end date]
  "endDate": "2018-01-02T14:11:27.00Z"        //[java.sql.Timestamp: end date must be after start date]
  "playerID": "1"                             //[Long: existing player id]
}
```

### ChatMessage
```javascript
{ 
  "message": "Some message"     //[String: size -> 3 to 100 letters]
}
```

### ResponseMessage
```javascript
{ 
  "httpCode": "404"             //[String: one of http codes]
  "message": "Not found"        //[String]
}
