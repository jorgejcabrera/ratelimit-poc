FROM openjdk:17-jdk-alpine

WORKDIR /app

RUN apk add tcpdump
RUN apk add curl
RUN wget -O dd-java-agent.jar https://dtdg.co/latest-java-tracer

COPY ./build/libs/ratelimit-poc-1.0-SNAPSHOT-all.jar service.jar
COPY ./config/servercfg.yml /app/config.yaml

CMD ["java", "-javaagent:dd-java-agent.jar", "-jar", "service.jar", "server", "config.yaml"]

EXPOSE 8080