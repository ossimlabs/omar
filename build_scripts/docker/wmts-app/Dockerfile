FROM ossimlabs/o2-base
RUN yum -y install o2-wmts-app && yum clean all
ADD wmts-app.yml /usr/share/omar/wmts-app/wmts-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    WFSSERVER=${WFSSERVER}\
    WFSPORT=${WFSPORT}\
    WMSSERVER=${WMSSERVER}\
    WMSPORT=${WMSPORT}\
    FOOTPRINTS=${FOOTPRINTS}\
    FOOTPRINTSPORT=${FOOTPRINTSPORT}

ARG O2_DEV_HOME

RUN rm -f /usr/share/omar/wmts-app/wmts-app.jar && \
  ln -s $O2_DEV_HOME/apps/wmts-app/build/libs/wmts-app-1.0.0.jar /usr/share/omar/wmts-app/wmts-app.jar

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wmts-app/wmts-app.sh"]
