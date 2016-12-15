# Welcome to the Avro Service for Docker

The Avro service takes an AVRO JSON payload or JSON record from and AVRO file as input and will process the file by looking for the reference URI field and downloading the File. The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields:

View the [Avro Service](../install-guide/avro-app.md#Installation) install guide for additional information on using the service.

## Dockerfile
```
# This service takes an AVRO JSON payload or JSON record
# from an AVRO file as input and will process the file
# by looking for the reference URI field and downloading
# the File.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="Takes an AVRO JSON \
      payload or JSON record from an AVRO file as \
      input and will process the file by looking for \
      the reference URI field and downloading the File."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-avro-app && yum clean all
ADD avro-app.yml /usr/share/omar/avro-app/avro-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    ADDRASTERENDPOINTURL=${ADDRASTERENDPOINTURL}\
    ADDRASTERENDPOINTPORT=${ADDRASTERENDPOINTPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/avro-app/avro-app.sh"]

```

## Docker Compose

Docker Compose official [docs](https://docs.docker.com/compose/overview/).

Navigate to the docker directory:

```
$ cd omar/build_scripts/docker
```

The directory should have a YAML file:

```
omar/build_scripts/docker/docker-compose.yml
```

Execute the following:

```
docker-compose build --no-cache avro
```

*Note: This will build only the **avro** service in the **docker-compose.yml** file.  It will pull in the dependency of the **o2-base** image, and create the **radiantbluetechnologies/avro** Docker image. It will **not** use cache when building the Docker image.*

Execute the following:
```
$ docker run -d -p 5000:8080 \
-e DBHOST='<YOUR_DB_HOST>' \
-e DBPORT='<YOUR_DB_PORT>' \
-e DBNAME='<YOUR_DB_NAME>' \
-e DBUSER='<YOUR_DB_USER>' \
-e DBPASS='<YOUR_DB_PASSWORD>' \
-e ADDRASTERENDPOINTURL='<YOUR_ADD_RASTER_URL>' \
-e ADDRASTERENDPOINTPORT='<YOUR_ADD_RASTER_PORT>' \
-i radiantbluetechnologies/avro
```

The **avro-app** should now be running in a new container.  

```
$ omar/build_scripts/docker/docker-compose.yml
```
## Modifying the docker-compose.yml
You will also need to modify the *environment* section of the **o2-avro** service within the [Docker Compose File for O2 Services](docker-common/#docker-compose-file-for-o2-services) with your local development parameters.
Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-avro** service should now be running in a new container.  

To list all running containers:

```
$ docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/avro  | "/bin/sh -c 'yum -y i" | 17 seconds ago | | small_borg

908c7ee6d152 | radiantbluetechnologies/o2-avro  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:5001->8080/tcp | o2-avro

To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it <containerID> bash
$ docker ps -a
```

Attaching to the container via bash (if needed):

```
$ docker exec -it o2-avro bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

---

##Verify that the avro-app is running correctly:

You will need to get your Docker host IP:
```
docker-machine ip
```

Note: You will also need to provide the port for the running container.  We specified port _5000_ in the *run* command above; however, if you were running this from Compose you could find the port by listing the running containers.

List containers:
```
docker ps
```

Look for the running radiantbluetechnologies/avro container.  It will have an associated port number.

Using your host IP and port from the commands above, test the *avro-app* service health status:
```
http://<YOUR_HOST_IP>:5000/health
```

##Verify that the o2-avro service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Note: You will also need to provide the port for the running container.  We specified port _5001_ in the compose file.

List containers:
```
$ docker ps
```

Look for the running radiantbluetechnologies/o2-avro container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-avro** service **health** status in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/health
```
You should receive:
`{"status":"UP"}`

You can also test the **Quartz Job** used to run the avro request:
```
http://<YOUR_HOST_IP>:5000/quartz/list
```

You should see a page with a digital clock and a count down timer.

#### Quartz
You can also test the **Quartz Job** used to run the **avro** request in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/quartz/list
```
You should see a page with a digital clock and a count down timer.

#### API
Access the **API** Page:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/api
```
The API page allows you to test various parts of the **o2-avro** service.  View the [Avro Service](../install-guide/avro-app.md#Installation) install guide for additional information on using the service.
