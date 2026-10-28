
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn -B -Dmaven.compiler.proc=full -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/vichu-meats-1.0.0.jar app.jar

EXPOSE 8080

ENV BIND_ADDRESS=0.0.0.0

ENTRYPOINT ["java", "-jar", "app.jar"]
 
