FROM amazoncorretto:21-alpine3.21-jdk

WORKDIR /app

COPY configuration/build/libs/delivery.jar delivery.jar

ENTRYPOINT ["java", "-jar", "delivery.jar"]