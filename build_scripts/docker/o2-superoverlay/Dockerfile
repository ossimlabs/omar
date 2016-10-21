# The Superoverlay service creates a KML overlay
# for any image found in the indexed database.
# The service calls the WMS chipping servcie and
# allows one to create KML lod nodes to partition
# the dataset into a hierarchical LOD grid.

FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The Superoverlay \
      service creates a KML overlay for any image \
      found in the indexed database. The service calls \
      the WMS chipping servcie and allows one to create \
      KML lod nodes to partition the dataset into a \
      hierarchical LOD grid."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-superoverlay-app && yum clean all
ADD superoverlay-app.yml /usr/share/omar/superoverlay-app/superoverlay-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    O2URL=${O2URL}\
    WMSURL=${WMSURL}\
    WMSPORT=${WMSPORT}\
    SPRING_CLOUD_CONFIG_ENABLED=true\
    SPRING_CLOUD_DISCOVERY_ENABLED=true

EXPOSE 8080
CMD ["sh", "/usr/share/omar/superoverlay-app/superoverlay-app.sh"]
