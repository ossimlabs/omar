FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WMTS Implements \
      the OGC WMTS standard.  The WMTS service uses \
      the JAVA bindings to the OSSIM core library to \
      perform all on-the-fly chipping of raw imagery \
      via a WMTS GetTile query."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
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

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wmts-app/wmts-app.sh"]
