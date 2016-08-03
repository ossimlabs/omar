# Welcome to the WFS Service for Docker
WFS Implements the OGC WFS standard. The Web Feature Service (WFS) supports returning feature information indexed into either the imagery tables or the video tables.

View the [WFS Service](../install-guide/wfs-app.md#Installation) install guide for additional information on using the service.

## Dockerfile
```
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
RUN yum -y install java-1.8.0-openjdk-devel && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_core-1.1.3.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_core-1.1.3.jar && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_codec-1.1.3.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_codec-1.1.3.jar && \
  curl -L http://s3.amazonaws.com/ossimlabs/dependencies/jai/jai_imageio-1.1.jar -o /usr/lib/jvm/java/jre/lib/ext/jai_imageio-1.1.jar && \
  yum -y install o2-wfs-app && yum clean all
ADD wfs-app.yml /usr/share/omar/wfs-app/wfs-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}
    # DBUSER=${DBUSER}\
    # DBPASS=${DBPASS}\
    # DBNAME=${DBNAME}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wfs-app/wfs-app.sh"]

```

## Docker Compose

Docker Compose official [docs](https://docs.docker.com/compose/overview/).

Navigate to the docker directory:

```
$ cd omar/build_scripts/docker
```

The directory should have a YAML file:

```
$ omar/build_scripts/docker/docker-compose.yml
```
## Modifying the docker-compose.yml
You will also need to modify the *environment* section of the **o2-wfs** service within the [Docker Compose File for O2 Services](docker-common/#docker-compose-file-for-o2-services) with your local development parameters.
