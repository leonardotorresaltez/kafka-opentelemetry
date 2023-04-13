FROM eclipse-temurin:17-jdk-alpine
RUN apk upgrade --no-cache && \
    apk add --no-cache bash openssl libgcc libstdc++ ncurses-libs 
VOLUME /tmp
ARG JAR_FILE
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]