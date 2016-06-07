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

### Building the Images Locally
To create or update new images, you'll likely want to build them locally
and then push them to the public registry once they're tested and working. You 
don't need to build the images to use them. They're already available in
the public docker registry. 

You can get them with a simple `$ docker pull radiantbluetechnologies/image-name`

If you do need to build new images, you can build each one manually, or 
all at once using `docker-compose`.

Below this current directory, you'll find individual directories that correspond
to each application:


    |--docker/

      |--wmts-app/

         |--Dockerfile

         |--wmts-app.yml

      |--wms-app/

         |--Dockerfile

         |--wms-app.yml
    ...

To build one image manually, from the app's directory, use `docker build`:

`$ docker build -t radiantbluetechnologies/app-name-app .`

To build all of the images at once, you can use `docker-compose` from the 
root `docker` directory:

`$ docker-compose up`

This will launch a local group of containers, properly linked together for
testing and development.

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

[Kubernetes](http://kubernetes.io) is used for container registration
and scheduling. It abstracts the management and placement of containers
within a cluster and also the discovery of those containers. It also manages
fail-over if a node crashes or is updated.

You can install a local Kubernetes cluster in using vagrant with
the [official guide](http://kubernetes.io/docs/getting-started-guides/vagrant/).
Using the instructions there, you will end up with a vagrant Kubernetes 
cluster using Fedora 23 as the host. If you'd like to use [CoreOS](http://coreos.com)
as the bas operating sytem, set the environment variable `KUBERNETES_OS=coreos`. 

You can set the following in your `~/.bashrc` to save some typing:

    # Kubernetes Local Development
    export KUBERNETES_PROVIDER=vagrant
    export NUM_NODES=3
    alias kubectl='/path/to/extracted/kubernetes.tar.gz/kubernetes/cluster/kubectl.sh'

Here are the basic step by step instructions:

1. Download [release v.1.3.0-alpha.5](https://github.com/kubernetes/kubernetes/releases/download/v1.3.0-alpha.5/kubernetes.tar.gz)
2. Extract it locally: 
  - `$ tar xvf kubernetes.tar.gz`
3. Set the Kubernetes provider environment variable:
  - `$ export KUBERNETES_PROVIDER=vagrant`
4. Start the cluster:
  - `$ kubernetes/cluster/kube-up.sh`

*Using CoreOS Vagrant provided: https://coreos.com/kubernetes/docs/latest/kubernetes-on-vagrant.html*

**As of 6/6/16 the release version of kubernetes does not work with Vagrant.
Using [v.1.3.0-alpha.5](https://github.com/kubernetes/kubernetes/releases/download/v1.3.0-alpha.5/kubernetes.tar.gz) is the recommded download.**

**The vagrant-cachier, vagrant-service-manager, and vagrant-registration are
not compatible with this installation method. You should unistall them
before using the Kubernetes box**

By default, the Kubernetes vagrant installation method creates two machines, 
a Kubernetes *master* and a Kubernetes *minion*. 
You can use these to verify your pods and services are working
correctly. See the [Kubernetes Documentation](http://kubernetes.io) for more
detailed information about *pods*, *services*, and clustering.

By default only a master and minion are created.

If you'd like more than one minion, set the environment variable `NUM_MINIONS`
to your desired amount:

`$ export NUM_NODES=2`

