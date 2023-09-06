# SPRING-APP
In this application, I show exactly what I can do as a developer.

## Tech stack
Java, Spring(WebFlux, Security), Gradle, Postgresql, Flyway

## Features

### Authentication and authorization service
Here I used Bearer/Jwt token.
Have two main flows of usage:
- User send a login and password. If all fine get JWT token.
- User send JWT token and get validation before getting info from protected endpoints.

### Endpoints
- "/register", "/login" - public api for auth
- "/info" - only authenticated user can get info about self.