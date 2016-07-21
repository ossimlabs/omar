# Welcome to the Avro Service for Docker

The Avro service takes an AVRO JSON payload or JSON record from and AVRO file as input and will process the file by looking for the reference URI field and downloading the File. The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields:

View the [Avro Service](../install-guide/avro-app.md#Installation) install guide for additional information on using the service.

<<<<<<< HEAD
=======
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

>>>>>>> dev
## Docker Compose

Docker Compose official [docs](https://docs.docker.com/compose/overview/).

Navigate to the docker directory:

```
<<<<<<< HEAD
omar/build_scripts/docker
=======
$ cd omar/build_scripts/docker
>>>>>>> dev
```

The directory should have a YAML file:

```
<<<<<<< HEAD
omar/build_scripts/docker/docker-compose.yml
```

Execute the following:

```
docker-compose build --no-cache avro
```

*Note: This will build only the **avro** service in the **docker-compose.yml** file.  It will pull in the dependency of the **o2-base** image, and create the **radiantbluetechnologies/avro** Docker image. It will **not** use cache when building the Docker image.*

Execute the following:
```
docker run -d -p 5000:8080 \
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
=======
$ omar/build_scripts/docker/docker-compose.yml
```
## Modifying the docker-compose.yml
You will also need to modify the *environment* section of the **o2-avro** service with your local development parameters.

```
version: '2'
services:
  o2-base:
    build: ./o2-base
    container_name: o2-base
    image: radiantbluetechnologies/o2-base
  o2-db:
    container_name: o2-db
    build: ./o2-db
    environment:
      POSTGRES_PASSWORD: abc123
      POSTGRES_USER: postgres_user
      POSTGRES_DB: omar_prod
    image: radiantbluetechnologies/o2-db
  o2-wmts:
    build: ./o2-wmts
    container_name: o2-wmts
    environment:
      DBHOST: o2-db
      DBPORT: :5432
      DBNAME: omar_prod
      DBUSER: postgres_user
      DBPASS: abc123
      WFSSERVER: o2.ossim.org/o2
      WFSPORT: ""
      WMSSERVER: o2.ossim.org/o2
      WMSPORT: ""
      FOOTPRINTS: o2.ossim.org/o2
      FOOTPRINTSPORT: ""
    ports:
      - "4999:8080"
    links:
      - o2-db
    image: radiantbluetechnologies/o2-wmts
    depends_on:
      - o2-base
      - o2-db
  o2-sqs:
    build: ./o2-sqs
    container_name: o2-sqs
    environment:
      AWSDNS: sqs.us-east-1.amazonaws.com
      AWSQUEUEPATH: 320588532383/avro-tst
      WAIT_TIME_SECONDS: 20
      NUMBER_OF_MESSAGES: 1
      POLLING_INTERVAL_SECONDS: 10
      DESTINATION_TYPE: stdout
      DESTINATION_POST_END_POINT: o2-avro
      DESTINATION_POST_END_POINT_PORT: :8080
      DESTINATION_POST_FIELD: message
    volumes:
      # Modify the path below to reflect your
      # AWS credentials location. Example:
      #- /Users/jdoe/.aws:/root/.aws
      - /Users/<yourusername>/.aws:/root/.aws
    ports:
      - "5000:8080"
    image: radiantbluetechnologies/o2-sqs
    depends_on:
      - o2-base
      - o2-stager
      - o2-avro
  o2-avro:
    build: ./o2-avro
    container_name: o2-avro
    environment:
      DBHOST: o2-db
      DBPORT: :5432
      DBNAME: omar_prod
      DBUSER: postgres_user
      DBPASS: abc123
      ADDRASTERENDPOINTURL: o2-stager
      ADDRASTERENDPOINTPORT: :8080
    volumes:
      # Mount the the host local data directory to the container
      - /Users/Shared/data:/data
    ports:
      - "5001:8080"
    links:
      - o2-db
    image: radiantbluetechnologies/o2-avro
    depends_on:
      - o2-base
      - o2-db
      - o2-stager
  o2-stager:
    build: ./o2-stager
    container_name: o2-stager
    environment:
      DBHOST: o2-db
      DBPORT: :5432
      DBNAME: omar_prod
      DBUSER: postgres_user
      DBPASS: abc123
    volumes:
      # Mount the the host local data directory to the container
      - /Users/Shared/data:/data
    ports:
      - "5002:8080"
    links:
      - o2-db
    image: radiantbluetechnologies/o2-stager
    depends_on:
      - o2-base
      - o2-db


```
Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **o2-avro** service should now be running in a new container.  
>>>>>>> dev

To list all running containers:

```
<<<<<<< HEAD
docker ps
=======
$ docker ps
>>>>>>> dev
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
<<<<<<< HEAD
908c7ee6d152 | radiantbluetechnologies/avro  | "/bin/sh -c 'yum -y i" | 17 seconds ago | | small_borg

=======
908c7ee6d152 | radiantbluetechnologies/o2-avro  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:5001->8080/tcp | o2-avro
>>>>>>> dev

To list all containers (even those not running):

```
<<<<<<< HEAD
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it <containerID> bash
=======
$ docker ps -a
```

Attaching to the container via bash (if needed):

```
$ docker exec -it o2-avro bash
>>>>>>> dev
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

---

<<<<<<< HEAD
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
=======
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
>>>>>>> dev
```
You should receive:
`{"status":"UP"}`

<<<<<<< HEAD
You can also test the **Quartz Job** used to run the avro request:
```
http://<YOUR_HOST_IP>:5000/quartz/list
```

You should see a page with a digital clock and a count down timer.
=======
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
>>>>>>> dev
