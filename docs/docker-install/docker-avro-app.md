# Welcome to the Avro Service for Docker

The Avro service takes an AVRO JSON payload or JSON record from and AVRO file as input and will process the file by looking for the reference URI field and downloading the File. The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields:

View the [Avro Service](../install-guide/avro-app.md#Installation) install guide for additional information on using the service.

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
You will need to modify the docker-compose.yml to bring up all of the necessary images. To do so you will need to comment out the other services that do _not_ pertain to the **avro** configuration.

You will also need to modify the *environment* section of the **avro** service with your local development parameters.

**Avro** only docker-compose.yml:
```
version: '2'
services:
  o2-base:
    build: ./o2-base
    image: radiantbluetechnologies/o2-base
  postgresdb:
    environment:
      POSTGRES_PASSWORD: abc123
      POSTGRES_USER: postgres_user
      POSTGRES_DB: omar_prod
    image: postgres:latest
  # wmts:
  #   environment:
  #     DBHOST: postgresdb
  #     DBPORT: :5432
  #     DBNAME: omar_prod
  #     DBUSER: postgres_user
  #     DBPASS: abc123
  #     WFSSERVER: o2.ossim.org/o2
  #     WFSPORT: ""
  #     WMSSERVER: o2.ossim.org/o2
  #     WMSPORT: ""
  #     FOOTPRINTS: o2.ossim.org/o2
  #     FOOTPRINTSPORT: ""
  #   build: ./wmts-app
  #   ports:
  #     - "5000:8080"
  #   links:
  #     - postgresdb
  #   image: radiantbluetechnologies/wmts
  #   depends_on:
  #     - o2-base
  #     - postgresdb
  # sqs:
  #   build: ./sqs-app
  #   environment:
  #     AWSDNS: sqs.us-east-1.amazonaws.com
  #     AWSQUEUEPATH: 320588532383/avro-tst
  #     WAIT_TIME_SECONDS: 20
  #     NUMBER_OF_MESSAGES: 1
  #     POLLING_INTERVAL_SECONDS: 10
  #     DESTINATION_TYPE: stdout
  #     DESTINATION_POST_END_POINT: ""
  #     DESTINATION_POST_FIELD: message
  #   volumes:
  #     # Modify the path below to reflect your
  #     # AWS credentials location
  #     - /Users/<yourusername>/.aws:/root/.aws
  #   ports:
  #     - "8080"
  #   image: radiantbluetechnologies/sqs
  #   depends_on:
  #     - o2-base
  # avrodb:
  #   environment:
  #     POSTGRES_PASSWORD: abc123
  #     POSTGRES_USER: avro
  #     POSTGRES_DB: omar_prod
  #   image: postgres:latest
  avro:
    build: ./avro-app
    container_name: avro_container
    environment:
      DBHOST: postgresdb
      DBPORT: :5432
      DBNAME: omar_prod
      DBUSER: postgres_user
      DBPASS: abc123
      ADDRASTERENDPOINTURL: <IP>
      ADDRASTERENDPOINTPORT: <:PORT>
    volumes:
      # Mount the data directory locally to the container
      - /Users/Shared/data://data
    ports:
      - "5000:8080"
    links:
      - postgresdb
    image: radiantbluetechnologies/avro
    depends_on:
      - o2-base
      - postgresdb

```
Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```

The **avro-app** should now be running in a new container.  

To list all running containers:

```
$ docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | radiantbluetechnologies/avro  | "/bin/sh -c 'yum -y i" | 17 seconds ago | | avro-container

To list all containers (even those not running):

```
$ docker ps -a
```

Attaching to the container via bash (if needed):

```
$ docker exec -it <containerID> bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

---

##Verify that the avro-app is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Note: You will also need to provide the port for the running container.  We specified port _5000_ in the compose file.

List containers:
```
$ docker ps
```

Look for the running radiantbluetechnologies/avro container.  It will have an associated port number.

Using your Docker host IP and port from the commands above, test the **avro-app** service **health** status in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/health
```
You should receive:
`{"status":"UP"}`

You can also test the **Quartz Job** used to run the avro request in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/quartz/list
```
You should see a page with a digital clock and a count down timer.

**API** Documentation:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/api
```
The API page allows you to test various parts of the **avro-app** service.  View the [Avro Service](../install-guide/avro-app.md#Installation) install guide for additional information on using the service.
