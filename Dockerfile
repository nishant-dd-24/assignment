FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -B dependency:go-offline

COPY src ./src
RUN ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# create non-root user
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# run with env-based JVM opts
USER app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]