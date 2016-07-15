# Welcome to the Stager Service for Docker

The stager service currently indexes the data into the OMAR system.

View the [Stager Service](../install-guide/stager-app.md#Installation) install guide for additional information on using the service.

```
# The stager service currently indexes the data into
# the OMAR system.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The stager service \
      currently indexes the data into the OMAR system."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-stager-app ossim-kakadu-plugin ossim-sqlite-plugin && yum clean all
ADD stager-app.yml /usr/share/omar/stager-app/stager-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/stager-app/stager-app.sh"]
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

Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-stager** service should now be running in a new container.  

To list all running containers:

```
docker ps
```
Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/o2-stager  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:5002->8080/tcp | o2-wmts


To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it o2-stager bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

##Verify that the o2-stager service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Look for the running radiantbluetechnologies/o2-stager container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-stager** service **health** status in a browser:
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
The API page allows you to test various parts of the **o2-stager** service.  View the [Stager Service](../install-guide/stager-app.md#Installation) install guide for additional information on using the service.
