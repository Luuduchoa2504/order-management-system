# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install Maven 4.0.0
RUN apt-get update && apt-get install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-4/4.0.0/binaries/apache-maven-4.0.0-bin.tar.gz && \
    tar -xzf apache-maven-4.0.0-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-4.0.0 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/order-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]