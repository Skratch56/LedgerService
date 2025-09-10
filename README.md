# Ledger Microservice

This repository contains the Ledger Spring Boot microservices and the Postgress db along with migration scripts:

- **Ledger Service** – Responsible for managing accounts and ledger transactions.

Both the micro service and db are packaged as Docker containers and can be run using Docker Compose.

---

## Prerequisites

- [Maven 3.9+](https://maven.apache.org/)
- [Java 21](https://adoptium.net/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## Building the JARs

From the project root, run:
This will build the JARs for both services under:

```bash
  mvn clean install 
```

This will generate the executable jar file ledger-service/target/ledger-service-<version>.jar

from there you run 

```bash
  docker network create payments-net
```
The above will create the network to allow for the two services to communicate

```bash
  docker-compose up --build 
```

This will start:

Postgres on port 5432

Ledger Service on port 8081

Then you will be able to access the api docs on http://localhost:8081/swagger-ui.html