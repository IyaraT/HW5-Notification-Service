FROM ubuntu:latest
LABEL authors="joe"

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY . .

ARG MODULE

RUN mvn -q -DskipTests package -pl ${MODULE} -am \
 && JAR=$(ls ${MODULE}/target/*.jar | grep -v '\.original$' | head -n 1) \
 && cp "$JAR" /build/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/app.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]