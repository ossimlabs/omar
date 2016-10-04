#!/bin/bash

# Locates script dir for relative paths:
if [ -z $SCRIPT_DIR ]; then
  pushd `dirname $0` >/dev/null
  export SCRIPT_DIR=`pwd -P`
  popd >/dev/null
fi

if [ -z $WORKSPACE ] ; then
   if [ -z $OSSIM_DEV_HOME ] ; then
      pushd $SCRIPT_DIR/../../.. >/dev/null
      export OSSIM_DEV_HOME=$PWD
      popd >/dev/null
   fi
else
   export OSSIM_DEV_HOME=$WORKSPACE
fi

# o2-base must be first for others depend on it
O2_APPS=( "o2-base" "o2-avro" "o2-db" "o2-download" "o2-jpip" "o2-jpip-server" "o2-mensa" "o2-omar" "o2-sqs" "o2-stager" "o2-superoverlay" "o2-swipe" "o2-wcs" "o2-web-proxy" "o2-wfs" "o2-wms" "o2-wmts" "tlv")
#pushd $SCRIPT_DIR
#O2_APPS=( "o2-base" )
#O2_APPS+=($(ls -d o2-* | sed -e "s/o2-base//g"))
#O2_APPS+=("tlv")
#popd

if [ -z $DOCKER_REGISTRY_URI ] ; then
  export DOCKER_REGISTRY_URI="320588532383.dkr.ecr.us-east-1.amazonaws.com"
fi

echo off
# Create login credentials for docker
if [[ "$DOCKER_REGISTRY_URI" =~ .*amazonaws.* ]] ; then
  eval `aws ecr get-login --region us-east-1`
  if [ $? != 0 ] ; then
    echo "Unable to create login credential for amazonaws access"
    exit 1
  fi
fi
echo on

export O2_APPS
export TAG="latest"

if [ -z $S3_DELIVERY_BUCKET ]; then
  export S3_DELIVERY_BUCKET="s3://o2-delivery/dev"
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


