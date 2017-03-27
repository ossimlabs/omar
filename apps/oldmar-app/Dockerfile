FROM REGISTRY_URI/PROJECT_PLACEHOLDER/o2-base:TAG_PLACEHOLDER
MAINTAINER DigitalGlobe-RadiantBlue

RUN useradd omar
COPY build/libs/oldmar-app-0.1.jar /home/omar

USER omar
WORKDIR /home/omar

EXPOSE 8080
CMD ["java", "-server", "-Xms256m", "-Xmx1024m", "-Djava.awt.headless=true", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseGCOverheadLimit", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/home/omar/oldmar-app-0.1.jar"]
