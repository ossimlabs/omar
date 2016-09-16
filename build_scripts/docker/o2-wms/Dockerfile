# WMS Implements the OGC WMS standard. The WMS service
# uses the JAVA bindings to the OSSIM core library to
# perform all on-the-fly chipping of raw imagery via a
# WMS GetMap query.

FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WMS Implements \
      the OGC WMS standard.  The WMS service uses \
      the JAVA bindings to the OSSIM core library to \
      perform all on-the-fly chipping of raw imagery \
      via a WMS GetMap query."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-wms-app && \
  yum -y install ossim && yum -y install ossim-kakadu-plugin && \
  yum -y install ossim-jpeg12-plugin && yum -y install ossim-sqlite-plugin && \
  yum -y install ossim-geopdf-plugin && \
  yum -y install ossim-png-plugin && yum -y install ossim-gdal-plugin.x86_64
ADD wms-app.yml /usr/share/omar/wms-app/wms-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}

RUN rm -f /usr/share/omar/wms-app/wms-app.jar && \
    ln -s /Users/sbortman/IdeaProjects/omar/apps/wms-app/build/libs/wms-app-1.0.0.jar /usr/share/omar/wms-app/wms-app.jar

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wms-app/wms-app.sh"]
