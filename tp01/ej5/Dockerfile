FROM eclipse-temurin:19-jre-alpine
COPY target/ej5-1.jar /usr/src/app.jar
COPY src/main/resources/geolite2-city-bd/* /usr/bd/
WORKDIR /usr/src/
EXPOSE 80/tcp
ENTRYPOINT ["java", "-jar", "app.jar"]