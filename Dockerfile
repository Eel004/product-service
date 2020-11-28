#
# Build stage
#
FROM maven:3.6.3-adoptopenjdk-14 AS build
COPY src /product-service/src
COPY pom.xml /product-service
RUN mvn -f /product-service/pom.xml clean package -Dmaven.test.skip

#
# Package stage
#
FROM adoptopenjdk/openjdk14
COPY --from=build /product-service/target/product-0.0.1-SNAPSHOT.jar /usr/local/lib/product-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=production","/usr/local/lib/product-0.0.1-SNAPSHOT.jar"]