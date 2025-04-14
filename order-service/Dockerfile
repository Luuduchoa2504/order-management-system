# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install Maven 3.9.9
RUN apt-get update && apt-get install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz && \
    tar -xzf apache-maven-3.9.9-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.9.9 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create directory for H2 database persistence
RUN mkdir -p /app/data

COPY --from=build /app/target/order-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]