FROM docker-registry-default.o2.radiantbluecloud.com/o2/o2-base
MAINTAINER DigitalGlobe-RadiantBlue
COPY build/libs/service-proxy-0.1.jar /usr/share
EXPOSE 8080
CMD ["java", "-server", "-Xms256m", "-Xmx1024m", "-Djava.awt.headless=true", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseGCOverheadLimit", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/usr/share/service-proxy-0.1.jar"]

