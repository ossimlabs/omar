#!/bin/bash
if [ -z $WORKSPACE ] ; then
   if [ -z $SCRIPT_DIR ] ; then
      pushd `dirname $0` >/dev/null
      export SCRIPT_DIR=`pwd -P`
      popd >/dev/null
   fi
   if [ -z $OSSIM_DEV_HOME ] ; then
      pushd $SCRIPT_DIR/../../.. >/dev/null
      export OSSIM_DEV_HOME=$PWD
      popd >/dev/null
   fi
else
   export OSSIM_DEV_HOME=$WORKSPACE
fi

# o2base must be first for others depend on it
#
O2_APPS=("o2-base")
#O2_APPS+=($(ls -d o2-* | sed -e "s/o2-base//g"))
O2_APPS+=($(ls -d o2-*))
O2_APPS+=("tlv")

if [ -z $DOCKER_REGISTRY_URI ] ; then
  export DOCKER_REGISTRY_URI="ossimlabs"
fi

export O2_APPS
export TAG="latest"

if [ -z $S3_DELIVERY_BUCKET ]; then
  export S3_DELIVERY_BUCKET="s3://o2-delivery/dev/docker"
  echo "WARNING: No URL specified for S3 delivery bucket. Defaulting S3_DELIVERY_BUCKET = <$S3_DELIVERY_BUCKET>"
  echo;
fi

function getImageName {
   imagename="${DOCKER_REGISTRY_URI}/$1:$2"
}

function runCommand {
  echo $*
  eval $*
  if [ $? != 0 ] ; then 
    echo "ERROR: Failed while executing command: <$*>."
    echo; exit 1;
  fi
}


