#!/bin/bash
export PROGRAM_PID=$1
export JPIP_DATA_DIR=${JPIPCACHEDIR}
export JPIP_SOURCES=20
export JPIP_CLIENTS=20
export JPIP_PORT=${JPIPSERVERPORT}
export JPIP_ADDRESS=$(hostname -I)
export JPIP_CONNECTION_THREADS=40
export JPIP_MAX_RATE=40000000
pushd `dirname $0` > /dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

if [ ! -d $JPIP_DATA_DIR ] ; then
mkdir -p $JPIP_DATA_DIR
fi

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
