FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN mvn -pl email-service -am clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /build/email-service/target/*.jar app.jar

EXPOSE 8002
ENTRYPOINT ["java","-jar","app.jar"]