FROM eclipse-temurin:21-jdk-alpine

COPY ./target/ledger-service-*.jar /ledger-service.jar

ENTRYPOINT ["sh", "-c", "java -jar /ledger-service.jar"]

