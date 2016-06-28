# Welcome to the SQS Service for Docker

The SQS service allows one to Read from an Amazon **S**imple **Q**ueueing **S**ystem (**SQS**) and redirect the messages either to standard out or to another URL endpoint by issuing a post.  We use a background job to poll the SQS for any pending messages and will only delete the messages from the SQS if the message was handled properly.  If the message was not handled properly it will remain on the queue and be reset automatically based on the queue properties.

View the [SQS Service](../install-guide/sqs-app.md#Installation) install guide for additional information on using the service.

## AWS Credentials

Currently we assume that the credentials are put in the proper location.  Please see the [AWS Credentials ](../install-guide/sqs-app.md#aws-credentials) section in the install guide to configure them properly.

## Docker Compose

Docker Compose official [docs](https://docs.docker.com/compose/overview/).

Modify the **volumes** attribute in the **docker-compose.yml** file located one directory above the **sqs-app** directory. Update the path to use **your** _credentials_ file location

Location:

```
omar/build_scripts/docker/docker-compose.yml
```

Example:

```  
volumes:
  # Modify the path below to reflect your
  # AWS credentials location
  - /Users/myusername/.aws:/root/.aws

```

Navigate to the docker directory:

```
omar/build_scripts/docker
```

Execute the following:

```
docker-compose build --no-cache sqs
```

*Note: This will build only the **sqs** service in the **docker-compose.yml** file.  It will pull in the dependency of the **o2-base** image, and create the **radiantbluetechnologies/sqs-app** Docker image. It will **not** use cache when building the Docker image.*

The **sqs-app** should now be running in a new container.  

To list all running containers:

```
docker ps
```

Output example:

CONTAINER ID | IMAGE | COMMAND | CREATED | PORTS | NAMES
------------ | ------------- | ------------ | ------------ | ------------ | ------------
908c7ee6d152 | 8e293390425e  | "/bin/sh -c 'yum -y i" | 17 seconds ago | | small_borg


To list all containers (even those not running):

```
docker ps -a
```

Attaching to the container via bash:

```
docker exec -it <containerID> bash
```
*Note: This will connect you to the container as `root@` the **containerID** you provide.*

---

Docker Build and Run can be used to manually build and run individual images and containers.

## Docker Build
Docker run official [docs](https://docs.docker.com/engine/reference/commandline/build/)

Navigate to the *sqs-app* directory:

```
docker build -t radiantbluetechnologies/sqs-app .
```
Note: Be sure not to forget the period (**.**) after the sqs-app at the end of the command.

## Docker Run
Docker run official [docs](https://docs.docker.com/engine/reference/run/).

*Note: Docker run also assumes you have your [AWS Credentials ](../install-guide/sqs-app.md#aws-credentials) installed.*

Navigate to the sqs-app directory:

```
omar/build_scripts/docker/sqs-app
```

Execute the following:
```
docker run -d -v /Users/yourusername/.aws://root/.aws -p 5000:8080 -i radiantbluetechnologies/sqs-app
```

##Verify that the sqs-app is running correctly:

You will need to get your Docker host IP:
```
docker-machine ip
```

Note: You will also need to provide the port for the running container.  We specified port _5000_ in the *run* command above; however, if you were running this from Compose you could find the port by listing the running containers.

List containers:
```
docker ps
```

Look for the running radiantbluetechnologies/sqs-app container.  It will have an associated port number.

Using your host IP and port from the commands above, test the *sqs-app* service health status:
```
http://<YOUR_HOST_IP>:5000/health
```
You should receive:
`{"status":"UP"}`

You can also test the **Quartz Job** used to run the SQS request:
```
http://<YOUR_HOST_IP>:5000/quartz/list
```

You should see a page with a digital clock and a count down timer.
