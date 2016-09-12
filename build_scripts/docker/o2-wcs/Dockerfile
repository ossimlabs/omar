# WCS Service Implements the OGC WCS standard.

FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WCS Service \
      Implements the OGC WCS standard."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-wcs-app \
    && yum -y install ossim \
    && yum -y install ossim-kakadu-plugin \
    && yum -y install ossim-jpeg12-plugin \
    && yum -y install ossim-sqlite-plugin \
    && yum -y install ossim-hdf5-plugin \
    && yum -y install ossim-geopdf-plugin \
    && yum -y install ossim-png-plugin \
    && yum -y install install ossim-gdal-plugin.x86_64 \
    && yum clean all
ADD wcs-app.yml /usr/share/omar/wcs-app/wcs-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wcs-app/wcs-app.sh"]
