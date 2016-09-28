#!/bin/bash

O2_APPS=($(ls -d o2-*))
O2_APPS+=("tlv")

export O2_APPS
export TAG="latest"

if [ -z $S3_DELIVERY_BUCKET ]; then
  export S3_DELIVERY_BUCKET="s3://o2-delivery/temp"
  echo "WARNING: No URL specified for S3 delivery bucket. Defaulting S3_DELIVERY_BUCKET = <$S3_DELIVERY_BUCKET>"
  echo;
fi

function getTarFileName {
   tarfilename="$1-$2.tgz"
}

function getImageName {
   imagename="ossimlabs/$1:$2"
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


