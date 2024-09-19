FROM amazoncorretto:17
MAINTAINER hitzseb
COPY target/petadoptapi-0.0.1-SNAPSHOT.jar elarcaapi
ENTRYPOINT ["java", "-jar", "elarcaapi"]