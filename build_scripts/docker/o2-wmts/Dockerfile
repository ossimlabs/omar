# WMTS implements the OGC WMTS standard. The WMTS web app
# uses the WMS and the WFS web services and assumes these
# services are reachable via a http "GET" call from the
# WMTS service. The WMTS service wraps the WMTS service
# call and 1) converts to a WFS query to get the features
# that cover the WMTS query parameters and 2) calls the
# WMS service to chip and return the pixel values that
# satisfy the WMTS request.

FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WMTS implements the \
      OGC WMTS standard. The WMTS web app uses the WMS \
      and the WFS web services and assumes these services \
      are reachable via a http 'GET' call from the WMTS \
      service. The WMTS service wraps the WMTS service \
      call and 1) converts to a WFS query to get the \
      features that cover the WMTS query parameters and \
      2) calls the WMS service to chip and return the \
      pixel values that satisfy the WMTS request."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-wmts-app && yum clean all
ADD wmts-app.yml /usr/share/omar/wmts-app/wmts-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    WFSSERVER=${WFSSERVER}\
    WFSPORT=${WFSPORT}\
    WMSSERVER=${WMSSERVER}\
    WMSPORT=${WMSPORT}\
    SPRING_CLOUD_CONFIG_ENABLED=true\
    SPRING_CLOUD_DISCOVERY_ENABLED=true


EXPOSE 8080

CMD ["sh", "/usr/share/omar/wmts-app/wmts-app.sh"]
