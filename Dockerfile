FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .

RUN sed -i 's/\r$//' mvnw \
    && chmod +x mvnw \
    && ./mvnw -B -Dmaven.compiler.proc=full -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/vichu-meats-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
