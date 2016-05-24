# Docker for O2
This directory contains various `Dockerfile`(s) to build containers for the 
*O2* applications. It includes a `docker-compose.yml` file to help building
and running the containers locally all at once.

## Local Development and Testing

### Setup
Depending on your development environment, you'll need to install either
the native Docker client and daemon, or use a virtual-machine. If you're one 
of the cool kids and use linux, you can install it directly using your
preferred package manager. If you are using Windows or Mac, you'll have to go
through some extra steps. Currently, the simplest way to get up and running
with Docker is to use [Docker Toolbox](https://www.docker.com/toolbox). Install
that first and once you're up and running, you can return here and start 
development and testing. 

### Building the Containers
You can build each container manually, or all at once using `docker-compose`.
Below this current directory, you'll find individual directories that correspond
to each application:


   |--  docker/
     |-- wmts-app/
       |-- Dockerfile
       |-- wmts-app.yml
     |-- wms-app/
       |-- Dockerfile
       |-- wms-app.yml

To build one container manually, from the app's directory, use `docker build`:

`$ docker build -t rbt/wms-app .`
