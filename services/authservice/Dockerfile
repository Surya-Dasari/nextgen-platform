FROM registry.access.redhat.com/ubi8/openjdk-17-runtime

WORKDIR /deployments

COPY target/authservice-0.0.1.jar app.jar

EXPOSE 9001

ENTRYPOINT ["java","-jar","/deployments/app.jar"]

