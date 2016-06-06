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

`$ docker build -t radiantbluetechnologies/wms-app .`

To build all of the containers at once, you can use `docker-compose` from the 
root `docker` directory:

`$ docker-compose up`

### Orchestrating the Containers with Swarm and Machine

You can quickly create an EC2 instance with [docker-machine](http://somelink.com)
and swarm:

See the [Official](https://docs.docker.com/swarm/provision-with-machine/) docs
for more information.

First, get a swarm token:

    $ docker run swarm create

    b5f099f9268f25239060b9bba60eba71

Now use this token to provision a master swarm node:

`$ docker-machine create --swarm --swarm-master --driver amazonec2 --amazonec2-ami ami-61bbf104 --amazonec2-ssh-user centos --swarm-discovery token://b5f099f9268f25239060b9bba60eba71 oc2s-dkr-dev-00`

And create a slave node:

`$ docker-machine create --swarm --driver amazonec2 --amazonec2-ami ami-61bbf104 --amazonec2-ssh-user centos --swarm-discovery token://b5f099f9268f25239060b9bba60eba71 oc2s-dkr-dev-01`

You can verify it worked by setting your environment to the master:

    $ eval "($ docker-machine env --swarm oc2s-dkr-dev-00)"`

    $ docker info

    Containers: 3
     Running: 3
     Paused: 0
     Stopped: 0
    Images: 2
    Server Version: swarm/1.2.3
    Role: primary
    Strategy: spread
    Filters: health, port, containerslots, dependency, affinity, constraint
    Nodes: 2
     oc2s-dkr-dev-00: 52.90.128.204:2376
      └ ID: VUQI:H6B5:LB32:D4IN:5GOA:TD4W:ND5K:EK25:UE7G:BHGQ:SMZZ:PLAW
      └ Status: Healthy
      └ Containers: 2
      └ Reserved CPUs: 0 / 1
      └ Reserved Memory: 0 B / 1.017 GiB
      └ Labels: executiondriver=, kernelversion=3.10.0-229.14.1.el7.x86_64, operatingsystem=CentOS Linux 7 (Core), provider=amazonec2, storagedriver=devicemapper
      └ UpdatedAt: 2016-05-31T11:44:02Z
      └ ServerVersion: 1.11.1
     oc2s-dkr-dev-01: 52.201.252.226:2376
      └ ID: HQRK:423J:7USY:WM3B:WACB:YCF7:4L5M:KUYC:LPOI:JPLV:TZZG:SHC6
      └ Status: Healthy
      └ Containers: 1
      └ Reserved CPUs: 0 / 1
      └ Reserved Memory: 0 B / 1.017 GiB
      └ Labels: executiondriver=, kernelversion=3.10.0-229.14.1.el7.x86_64, operatingsystem=CentOS Linux 7 (Core), provider=amazonec2, storagedriver=devicemapper
      └ UpdatedAt: 2016-05-31T11:44:17Z
      └ ServerVersion: 1.11.1
    Plugins: 
     Volume: 
     Network: 
    Kernel Version: 3.10.0-229.14.1.el7.x86_64
    Operating System: linux
    Architecture: amd64
    CPUs: 2
    Total Memory: 2.035 GiB
    Name: 611e8d32ee75
    Docker Root Dir: 
    Debug mode (client): false
    Debug mode (server): false
    WARNING: No kernel memory limit support
 
Connect locally to the swarm:

`eval $(docker-machine env --swarm oc2s-dkr-dev-00)`

Pull the latest app from the public registry:

`$ docker pull radiantbluetechnologies/wmts`

Now use `docker-compose` to bring up the containers:

`$ docker-compose up -d`

## A local Kubernetes Cluster

You can install a local Kubernetes cluster in vagrant with the [official guide](http://kubernetes.io/docs/getting-started-guides/vagrant/)
