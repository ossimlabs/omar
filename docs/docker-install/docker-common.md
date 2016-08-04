# Welcome to the OMAR Web Services for Docker

This directory contains various `Dockerfile`(s) to build containers for the
*OMAR Web Services* applications.

## Local Development and Testing
Depending on your development environment, you'll need to install either
the native Docker client and daemon, or use a virtual-machine. If you're one
of the cool kids and use linux, you can install it directly using your
preferred package manager. If you are using Windows or Mac, you'll have to go
through some extra steps. Currently, the simplest way to get up and running
with Docker is to use [Docker Toolbox](https://www.docker.com/toolbox). Install
that first and once you're up and running, you can return here and start
development and testing.

## Apple Mac issue

There is an issue with Docker on the Mac. View the issue on Github [here](https://github.com/docker/kitematic/issues/1182).

You may receive the following when attempting to run a Docker command in a terminal window/tab:

`Cannot connect to the Docker daemon. Is the docker daemon running on this host?`

#### Manual fix
In order to get Docker to work properly from the terminal you will need to run the following for each new terminal window or tab you open. Keep in mind that you will have to repeat this for each subsequent terminal window/tab your run.

```
eval "$(docker-machine env default)"
```

*The following two work arounds will not require you to manually enter the `eval` command above.*

#### iTerm2
iTerm2 user's you can simply add the command above to a Profile.  Do so by choosing an existing Profile, or creating a new one.  Add it to the
`Send text at start` parameter under `Profiles > General > Command`

#### Edit Bash profile
You can also add the following to your `~/.bashrc` profile to fix the problem:
```
docker_running=$(docker-machine ls | grep default)
if [[ "$docker_running" == *"Stopped"* ]]
then
    eval "$(docker-machine env default)"
elif [[ "$docker_running" == *"Running"* ]]
then
    eval "$(docker-machine env default)"
fi
```

## Building the Images Locally
To create or update new images, you'll likely want to build them locally and then push them to the public registry once they're tested and working. You don't need to build the images to use them. They're already available in the public docker registry.

A list of the RadiantBlue Technologies public images on the Docker Hub can be found [here](https://hub.docker.com/u/radiantbluetechnologies/).

You can get them with a simple:
```
docker pull radiantbluetechnologies/image-name
```

## OMAR Docker structure
The OMAR Docker structure can be found [here](https://github.com/ossimlabs/omar/tree/master/build_scripts/docker).  You will find individual directories that correspond to each application:

```
|--docker/

  |--kube-configs/

  |--wmts-app/

     |--Dockerfile

     |--wmts-app.yml

  |--sqs-app/

     |--Dockerfile

     |--sqs-app.yml
...
```

## Building individual images

To build one image manually, from the app's directory, use `docker build`:

Based on the directory structure above you will need to navigate to an app directory.

Example:
```
cd sqs-app
```

Execute the following:
```
docker build -t radiantbluetechnologies/sqs-app .
```
*Note: Be sure not to forget the period (.) after the **sqs-app** at the end of the command.*

## Running individual images:
```
docker run -d -v /Users/yourusername/.aws://root/.aws -p 5000:8080 -i radiantbluetechnologies/sqs-app
```
Note: The command above will **run** the newly created **radiantbluetechnologies/sqs-app** image in detached mode (-d), map a volume from the host to the container (-v), and specify that the containers port should be mapped to port 5000 on the host (-p).

---

## Building all the images at once with Compose

If you do need to build new images, you can build each one manually, or all at once using `docker-compose`.

The OMAR Docker structure includes a `docker-compose.yml` file.  Docker Compose allows you to build all of the images at one time.  This saves time by not having to type the individual build commands for each image like we did above with `docker run`.

View the official Docker Compose documentation [here](https://docs.docker.com/compose/).

## Docker Compose File for O2 Services
```
version: '2'
services:
  o2-base:
    build: ./o2-base
    container_name: o2-base
    image: radiantbluetechnologies/o2-base
  o2-db:
    build: ./o2-db
    container_name: o2-db
    environment:
      POSTGRES_USER: postgres_user
      POSTGRES_PASSWORD: abc123
      POSTGRES_DB: omar_prod
    ports:
      - "4990:5432"
    image: radiantbluetechnologies/o2-db
  o2-wms:
    build: ./o2-wms
    container_name: o2-wms
    environment:
      DBHOST: o2-db
      DBPORT: :5432
    #   DBNAME: omar_prod
    #   DBUSER: postgres_user
    #   DBPASS: abc123
    ports:
      - "4997:8080"
    links:
      - o2-db
    image: radiantbluetechnologies/o2-wms
    depends_on:
      - o2-base
      - o2-db
      - o2-stager
  # o2-wfs:
  #   build: ./o2-wfs
  #   container_name: o2-wfs
  #   environment:
  #     DBHOST: o2-db
  #     DBPORT: :5432
  #   #   DBNAME: omar_prod
  #   #   DBUSER: postgres_user
  #   #   DBPASS: abc123
  #   ports:
  #     - "4998:8080"
  #   links:
  #     - o2-db
  #   image: radiantbluetechnologies/o2-wfs
  #   depends_on:
  #     - o2-base
  #     - o2-db
  #     - o2-stager
  # o2-wmts:
  #   build: ./o2-wmts
  #   container_name: o2-wmts
  #   environment:
  #     DBHOST: o2-db
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
  #   ports:
  #     - "4999:8080"
  #   links:
  #     - o2-db
  #   image: radiantbluetechnologies/o2-wmts
  #   depends_on:
  #     - o2-base
  #     - o2-db
  # o2-sqs:
  #   build: ./o2-sqs
  #   container_name: o2-sqs
  #   environment:
  #     AWSDNS: sqs.us-east-1.amazonaws.com
  #     AWSQUEUEPATH: 320588532383/avro-tst
  #     WAIT_TIME_SECONDS: 20
  #     NUMBER_OF_MESSAGES: 1
  #     POLLING_INTERVAL_SECONDS: 10
  #     DESTINATION_TYPE: stdout
  #     DESTINATION_POST_END_POINT: o2-avro
  #     DESTINATION_POST_END_POINT_PORT: :8080
  #     DESTINATION_POST_FIELD: message
  #   volumes:
  #     # Modify the path below to reflect your
  #     # AWS credentials location. Example:
  #     #- /Users/jdoe/.aws:/root/.aws
  #     - /Users/<yourusername>/.aws:/root/.aws
  #   ports:
  #     - "5000:8080"
  #   image: radiantbluetechnologies/o2-sqs
  #   depends_on:
  #     - o2-base
  #     - o2-stager
  #     - o2-avro
  # o2-avro:
  #   build: ./o2-avro
  #   container_name: o2-avro
  #   environment:
  #     DBHOST: o2-db
  #     DBPORT: :5432
  #     DBNAME: omar_prod
  #     DBUSER: postgres_user
  #     DBPASS: abc123
  #     ADDRASTERENDPOINTURL: o2-stager
  #     ADDRASTERENDPOINTPORT: :8080
  #   volumes:
  #     # Mount the the host local data directory to the container
  #     - /Users/Shared/data:/data
  #   ports:
  #     - "5001:8080"
  #   links:
  #     - o2-db
  #   image: radiantbluetechnologies/o2-avro
  #   depends_on:
  #     - o2-base
  #     - o2-db
  #     - o2-stager
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

You will need to be in the docker directory under build_scripts:
```
cd omar/build_scripts/docker
```

Use Docker Compose:
```
docker-compose up
```

## Running a specific image with Compose
```
docker-compose run --service-ports avro bash
```
This will run the **avro** service in the docker-compose.yml file.  We are also passing the --service-ports flag which tells docker to create the ports specified in the compose file and map them to the host.

## Common commands

You will probably find you will use these commands repeatedly when working with Docker.

List running containers:
```
docker ps
```

List all containers (running or not):
```
docker ps -a
```

Start a container:
```
docker start <containerID>
```

Stop a container:
```
docker stop <containerID>
```

Remove a container:
```
docker rm <containerID>
```

Attach to a container via Bash
```
docker exec -it <containerID> bash
```

List images:
```
docker images
```

Remove an image.  Make sure it is not being used by any running containers.
```
docker rmi <imageID>
```
