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

Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-wfs** service should now be running in a new container.

To list all running containers:

```
docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/o2-wfs  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:4998->8080/tcp | o2-wmts

To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it o2-wfs bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*


##Verify that the o2-wmts service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Look for the running radiantbluetechnologies/o2-wfs container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-wfs** service **health** status in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/health
```
You should receive:
`{"status":"UP"}`

#### API
Access the **API** Page:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/api
```
The API page allows you to test various parts of the **o2-wfs** service.  View the [WFS Service](../install-guide/wfs-app.md#Installation) install guide for additional information on using the service.
