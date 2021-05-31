FROM gradle:6.7-jdk11
COPY build.gradle.kts .
COPY src ./src
RUN gradle clean build
EXPOSE 8080
ENTRYPOINT ["gradle", "run"]
