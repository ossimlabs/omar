# WFS Implements the OGC WFS standard. The Web Feature
# Service (WFS) supports returning feature information
# indexed into either the imagery tables or the video tables.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WFS Implements \
      the OGC WFS standard.  The Web Feature Service \
      (WFS) supports returning feature information \
      indexed into either the imagery tables or the \
       video tables."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-wfs-app && yum clean all
ADD wfs-app.yml /usr/share/omar/wfs-app/wfs-app.yml
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
