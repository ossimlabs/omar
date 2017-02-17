FROM REGISTRY_URI/o2-base:TAG_PLACEHOLDER
MAINTAINER DigitalGlobe-RadiantBlue

ENV USER_NAME omar\
    APP_HOME /usr/share

RUN useradd -ms /bin/bash $USER_NAME &&\
    mkdir -p $APP_HOME

COPY build/libs/service-proxy-0.1.jar $APP_HOME

USER $USER_NAME
WORKDIR $APP_HOME

EXPOSE 8080
CMD ["java", "-server", "-Xms256m", "-Xmx1024m", "-Djava.awt.headless=true", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseGCOverheadLimit", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/usr/share/service-proxy-0.1.jar"]
