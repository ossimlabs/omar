# WFS Implements the OGC WFS standard. The Web Feature
# Service (WFS) supports returning feature information
# indexed into either the imagery tables or the video tables.

FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="WFS Implements \
      the OGC WFS standard.  The Web Feature Service \
      (WFS) supports returning feature information \
      indexed into either the imagery tables or the \
       video tables."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install java-1.8.0-openjdk-devel ossim-oms && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_core-1.1.3.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_core-1.1.3.jar && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_codec-1.1.3.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_codec-1.1.3.jar && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_imageio-1.1.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_imageio-1.1.jar && \
  yum -y install o2-wfs-app && yum clean all
ADD wfs-app.yml /usr/share/omar/wfs-app/wfs-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    OSSIM_DATA=${OSSIM_DATA}\
    SPRING_CLOUD_CONFIG_ENABLED=${SPRING_CLOUD_CONFIG_ENABLED}\
    SPRING_CLOUD_DISCOVERY_ENABLED=${SPRING_CLOUD_DISCOVERY_ENABLED}


EXPOSE 8080
CMD ["sh", "/usr/share/omar/wfs-app/wfs-app.sh"]
