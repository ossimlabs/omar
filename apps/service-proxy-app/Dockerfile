FROM docker-registry-default.o2.radiantbluecloud.com/o2/o2-base
MAINTAINER DigitalGlobe-RadiantBlue
COPY build/libs/service-proxy-0.1.jar /usr/share
EXPOSE 8080
CMD ["java","-jar","/usr/share/service-proxy-0.1.jar"]
