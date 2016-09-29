#!/bin/bash
if [ -z $WORKSPACE ] ; then
   if [ -z $SCRIPT_DIR ] ; then
      pushd `dirname $0` >/dev/null
      export SCRIPT_DIR=`pwd -P`
   fi
   if [ -z $OSSIM_DEV_HOME ] ; then
      pushd $SCRIPT_DIR/../../.. >/dev/null
      export OSSIM_DEV_HOME=$PWD
      popd > /dev/null
   fi
   popd >/dev/null

else
   export OSSIM_DEV_HOME=$WORKSPACE
fi


O2_APPS=($(ls -d o2-*))
O2_APPS+=("tlv")

if [ -z $DOCKER_URI] ; then
  export DOCKER_URI="ossimlabs"
fi

export O2_APPS
export TAG="latest"

if [ -z $S3_DELIVERY_BUCKET ]; then
  export S3_DELIVERY_BUCKET="s3://o2-delivery/dev/docker"
  echo "WARNING: No URL specified for S3 delivery bucket. Defaulting S3_DELIVERY_BUCKET = <$S3_DELIVERY_BUCKET>"
  echo;
fi

function getTarFileName {
   tarfilename="$1-$2.tgz"
}

function getImageName {
   imagename="${DOCKER_URI}/$1:$2"
}

function runCommand() 
{
  echo $*
  eval $*
  if [ $? != 0 ] ; then 
    echo "ERROR: Failed while executing command: <$*>."
    echo; exit 1;
  fi
}


