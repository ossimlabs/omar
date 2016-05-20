# Welcome to JPIP Streaming service
The JPIP streaming service allows one to stream a directory of JP2 images to a client via a continuous socket or stateless http requests.


##Installation
We assume you have read the generalized installation procedures that shows the common configuration created for all services in the OMAR distribution found in the [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install ossim-jpip-server
```

The JPIP streaming service is a little different in implementation because it is a C++ daemon for implementing the JPIP protocol so not all the items in the [OMAR Common Install Guide](common.md) will apply to this document.  We will not have an external yaml config and all that is needed after the yum installation is a shell to run the application


The installation sets up

* Startup scripts that include /etc/init.d/jpip-server for init.d support and /usr/lib/systemd/system/jpip-server.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/jpip-server
* Creates a var run directory with user *omar* permissions under /var/run/jpip-server

The installation here is a daemon running a C++ application called ossim-jpip-server.  The yum installation will:

* Create a user called *omar*
* Setup the service scripts

We now need to Create the directory: 

```
sudo mkdir -p /usr/share/omar/jpip-server
```

set the permissions:

```
sudo chown omar:omar /usr/share/omar
```


create the shell script jpip-server.sh to the location /usr/share/omar/jpip-server/jpip-server.sh


```
#!/bin/bash
export PROGRAM_PID=$1
export JPIP_DATA_DIR=/data/jpip-cache
export JPIP_SOURCES=20
export JPIP_CLIENTS=20
export JPIP_PORT=8080
export JPIP_ADDRESS=192.168.2.108
export JPIP_CONNECTION_THREADS=40
export JPIP_MAX_RATE=40000000
pushd `dirname $0` > /dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

#Set working directory
pushd $JPIP_DATA_DIR
if [ -z $PROGRAM_PID ]; then
ossim-jpip-server -sources ${JPIP_SOURCES} -clients ${JPIP_CLIENTS} -port ${JPIP_PORT} -max_rate ${JPIP_MAX_RATE} -address ${JPIP_ADDRESS} -connection_threads ${JPIP_CONNECTION_THREADS}
else
ossim-jpip-server -sources ${JPIP_SOURCES} -clients ${JPIP_CLIENTS} -port ${JPIP_PORT} -max_rate ${JPIP_MAX_RATE} -address ${JPIP_ADDRESS} -connection_threads ${JPIP_CONNECTION_THREADS}&
sleep 1
echo $! >$PROGRAM_PID
fi
popd
```

The reason we don't do this for the rpm installation is that we do not know before hand where the jp2 files reside and be served up over the stream.

Now we should be able to start the service

```
sudo service jpip-server start
```

or if we are using systemd

```
sudo systemctl start jpip-server
```
