FROM eclipse-temurin:19-jre-alpine
COPY target/*dependencies.jar /app/app.jar
WORKDIR /app/
EXPOSE 9001/tcp
ENTRYPOINT ["java", "-jar", "app.jar"]