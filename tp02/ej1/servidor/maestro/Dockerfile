FROM eclipse-temurin:19-jre-alpine
COPY target/*dependencies.jar /app/app.jar
WORKDIR /app/
EXPOSE 8080/tcp
ENTRYPOINT ["/bin/sh", "-c", "java -jar /app/app.jar"]