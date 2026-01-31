FROM registry.access.redhat.com/ubi8/openjdk-17-runtime

WORKDIR /deployments

COPY target/apiservice-0.0.1.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java","-jar","/deployments/app.jar"]

