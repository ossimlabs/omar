# Welcome to the WMS Service for Docker

WMS Implements the OGC WMS standard. The WMS service uses the JAVA bindings to the OSSIM core library to perform all on-the-fly chipping of raw imagery via a WMS GetMap query.

View the [WMS Service](../install-guide/wms-app.md#Installation) install guide for additional information on using the service.

## Dockerfile
```
# WMS Implements the OGC WMS standard. The WMS service
# uses the JAVA bindings to the OSSIM core library to
# perform all on-the-fly chipping of raw imagery via a
# WMS GetMap query.

FROM radiantbluetechnologies/o2-base
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
  yum -y install ossim-hdf5-plugin && yum -y install ossim-geopdf-plugin && \
  yum -y install ossim-png-plugin && yum -y install ossim-gdal-plugin.x86_64
ADD wms-app.yml /usr/share/omar/wms-app/wms-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}
    # DBUSER=${DBUSER}\
    # DBPASS=${DBPASS}\
    # DBNAME=${DBNAME}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/wms-app/wms-app.sh"]

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
You will also need to modify the *environment* section of the **o2-wms** service within the [Docker Compose File for O2 Services](docker-common/#docker-compose-file-for-o2-services) with your local development parameters.

un docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-wms** service should now be running in a new container.

To list all running containers:

```
docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/o2-wms  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:4998->8080/tcp | o2-wms

To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:
```
docker exec -it o2-wms bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

##Verify that the o2-wms service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Look for the running radiantbluetechnologies/o2-wms container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-wms** service **health** status in a browser:
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
The API page allows you to test various parts of the **o2-wms** service.  View the [WMS Service](../install-guide/wms-app.md#Installation) install guide for additional information on using the service.
