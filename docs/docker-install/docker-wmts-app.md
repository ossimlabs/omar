<<<<<<< HEAD
TODO:
Add docs for wmts
=======
# Welcome to the WMTS Service for Docker

WMTS implements the OGC WMTS standard. The WMTS web app uses the WMS and the WFS web services and assumes these services are reachable via a http "GET" call from the WMTS service. The WMTS service wraps the WMTS service call and 1) converts to a WFS query to get the features that cover the WMTS query parameters and 2) calls the WMS service to chip and return the pixel values that satisfy the WMTS request.

View the [WMTS Service](../install-guide/wmts-app.md#Installation) install guide for additional information on using the service.

## Dockerfile
```
# WMTS implements the OGC WMTS standard. The WMTS web app
# uses the WMS and the WFS web services and assumes these
# services are reachable via a http "GET" call from the
# WMTS service. The WMTS service wraps the WMTS service
# call and 1) converts to a WFS query to get the features
# that cover the WMTS query parameters and 2) calls the
# WMS service to chip and return the pixel values that
# satisfy the WMTS request.

FROM radiantbluetechnologies/o2-base
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
    WFSSERVER=${WFSSERVER}\
    WFSPORT=${WFSPORT}\
    WMSSERVER=${WMSSERVER}\
    WMSPORT=${WMSPORT}\
    FOOTPRINTS=${FOOTPRINTS}\
    FOOTPRINTSPORT=${FOOTPRINTSPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wmts-app/wmts-app.sh"]

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
You will also need to modify the *environment* section of the **o2-wmts** service within the [Docker Compose File for O2 Services](docker-common/#docker-compose-file-for-o2-services) with your local development parameters.

Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-wmts** service should now be running in a new container.  

To list all running containers:

```
docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/o2-wmts  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:4999->8080/tcp | o2-wmts


To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it o2-wmts bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

##Verify that the o2-wmts service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Look for the running radiantbluetechnologies/o2-wmts container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-wmts** service **health** status in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/health
```
You should receive:
`{"status":"UP"}`

#### WMTS Viewer
Access the "O2 | WMTS Viewer":
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/wmtsApp/index
```
The WMTS viewer displays the images on http://o2.ossim.org in WMTS format.  You can zoom in to individual footprints to see them rendered as WMTS tiles.

#### API
Access the **API** Page:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/api
```
The API page allows you to test various parts of the **o2-wmts** service.  View the [WMTS Service](../install-guide/wmts-app.md#Installation) install guide for additional information on using the service.
>>>>>>> dev
