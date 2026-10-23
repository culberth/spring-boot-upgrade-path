# Build the jar first: mvn package
FROM eclipse-temurin:17-jre AS builder
WORKDIR /application
COPY target/catalog-service-*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:17-jre
WORKDIR /application
COPY --from=builder /application/extracted/dependencies/ ./
COPY --from=builder /application/extracted/spring-boot-loader/ ./
COPY --from=builder /application/extracted/snapshot-dependencies/ ./
COPY --from=builder /application/extracted/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
