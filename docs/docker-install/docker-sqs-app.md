# Welcome to the SQS Service for Docker

The SQS service allows one to Read from an Amazon **S**imple **Q**ueueing **S**ystem (**SQS**) and redirect the messages either to standard out or to another URL endpoint by issuing a post.  We use a background job to poll the SQS for any pending messages and will only delete the messages from the SQS if the message was handled properly.  If the message was not handled properly it will remain on the queue and be reset automatically based on the queue properties.

View the [SQS Service](../install-guide/sqs-app.md#Installation) install guide for additional information on using the service.

## AWS Credentials

Currently we assume that the credentials are put in the proper location.  Please see the [AWS Credentials ](../install-guide/sqs-app.md#aws-credentials) section in the install guide to configure them properly.

## Dockerfile
```
# The SQS service allows one to Read from an Amazon Simple Queueing
# System (SQS) and redirect the messages either to standard out or
# to another URL endpoint by issuing a post. We use a background
# job to poll the SQS for any pending messages and will only
# delete the messages from the SQS if the message was handled
# properly. If the message was not handled properly it will
# remain on the queue and be reset automatically based on the queue
# properties.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The SQS service allows \
      one to Read from an Amazon Simple Queueing System (SQS) \
      and redirect the messages either to standard out or to \
      another URL endpoint by issuing a post. We use a background \
      job to poll the SQS for any pending messages and will only \
      delete the messages from the SQS if the message was handled \
      properly. If the message was not handled properly it will \
      remain on the queue and be reset automatically based on the \
      queue properties."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-sqs-app && yum clean all
ADD sqs-app.yml /usr/share/omar/sqs-app/sqs-app.yml
ENV AWSDNS=${AWSDNS}\
    AWSQUEUEPATH=${AWSQUEUEPATH}\
    WAIT_TIME_SECONDS=${WAIT_TIME_SECONDS}\
    NUMBER_OF_MESSAGES=${NUMBER_OF_MESSAGES}\
    DESTINATION_TYPE=${DESTINATION_TYPE}\
    DESTINATION_POST_END_POINT=${DESTINATION_POST_END_POINT}\
    DESTINATION_POST_END_POINT_PORT=${DESTINATION_POST_END_POINT_PORT}\
    DESTINATION_POST_FIELD=${DESTINATION_POST_FIELD}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/sqs-app/sqs-app.sh"]

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

Modify the **volumes** attribute in the **docker-compose.yml** file. Update the path to use **your** _credentials_ file location

Location:

```
omar/build_scripts/docker/docker-compose.yml
```

Example:

```  
volumes:
  # Modify the path below to reflect your
  # AWS credentials location
  - /Users/<yourusername>/.aws:/root/.aws

```

Run docker compose up to build/run the images and containers:
```
$ docker-compose up
```


The **o2-sqs** service should now be running in a new container.  

To list all running containers:

```
docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | 8e293390425e  | "/bin/sh -c 'yum -y i" | 17 seconds ago | 0.0.0.0:5000->8080/tcp | o2-sqs


To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it o2-sqs bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

---

##Verify that the o2-sqs service is running correctly

You will need to get your Docker host IP:
```
$ docker-machine ip
```

Look for the running radiantbluetechnologies/o2-sqs container.  It will have an associated port number.

#### Health Check
Using your Docker host IP and port from the commands above, test the **o2-sqs** service **health** status in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/health
```
You should receive:
`{"status":"UP"}`

#### Quartz
You can also test the **Quartz Job** used to run the **SQS** requests in a browser:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/quartz/list
```
You should see a page with a digital clock and a count down timer.

#### API
Access the **API** Page:
```
http://<YOUR_DOCKER_HOST_IP>:<YOUR_DOCKER_HOST_PORT>/api
```
The API page allows you to test various parts of the **o2-sqs** service.  View the [SQS Service](../install-guide/sqs-app.md#Installation) install guide for additional information on using the service.
