#!/bin/bash

# Assumes AWS account "rbt-modapps" (320588532383) UNLESS env var
# USE_C2S_ACCOUNT exists and is set to "true", in which case the rbt-c2s
# (433455360279) AWS account is used.

# Locates script dir for relative paths:
if [ -z $OMAR_SCRIPT_DIR ]; then
  pushd `dirname $0` >/dev/null
  OMAR_SCRIPT_DIR=`pwd -P`
  popd >/dev/null
fi
echo "SCRIPT DIRECTORY==============$OMAR_SCRIPT_DIR"
if [ -z $WORKSPACE ] ; then
   if [ -z $OSSIM_DEV_HOME ] ; then
      pushd $OMAR_SCRIPT_DIR/../../.. >/dev/null
      export OSSIM_DEV_HOME=$PWD
      popd >/dev/null
   fi
else
   export OSSIM_DEV_HOME=$WORKSPACE
fi
pushd $OMAR_SCRIPT_DIR/../linux
source ./git-prompt.sh
popd

if [ -z $OSSIM_GIT_BRANCH ] ; then
   pushd $OMAR_SCRIPT_DIR
   export OSSIM_GIT_BRANCH=`__git_ps1 "%s"`
   popd $OSSIM_DEV_HOME/ossim-ci
fi

# o2-base must be first for others depend on it
O2_APPS=( "o2-base" "o2-avro" "o2-db" "o2-disk-cleanup" "o2-download" "o2-jpip" "o2-jpip-server" "o2-mensa" "o2-omar" "o2-sqs" "o2-stager" "o2-superoverlay" "o2-wcs" "o2-web-proxy" "o2-wfs" "o2-wms" "o2-wmts" "tlv")
#pushd $OMAR_SCRIPT_DIR
#O2_APPS=( "o2-base" )
#O2_APPS+=($(ls -d o2-* | sed -e "s/o2-base//g"))
#O2_APPS+=("tlv")
#popd

if [ -z $AWS_ACCOUNT_ID ]; then
  if [ "$USE_C2S_ACCOUNT" == true ]; then
    export AWS_ACCOUNT_ID="433455360279"
  else
    export AWS_ACCOUNT_ID="320588532383"
  fi
fi
if [ -z $AWS_DEFAULT_REGION ] ; then
  export AWS_DEFAULT_REGION="us-east-1"
fi

# URL of docker container image registry.
# NOTE: Jenkins needs authentication on specified registry.
if [ -z $DOCKER_REGISTRY_URI ] ; then
  export DOCKER_REGISTRY_URI="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
fi

# Create login credentials for docker
if [[ "$DOCKER_REGISTRY_URI" =~ .*amazonaws.* ]] ; then
  echo; echo "Attempting to log into Amazon container registry..."
  eval `aws ecr get-login --region us-east-1`
  if [ $? != 0 ] ; then
    echo "Unable to create login credential for amazonaws access"
    exit 1
  fi
fi

export O2_APPS
export TAG="latest"

if [ "${OSSIM_GIT_BRANCH}" == "master" ] ; then
  export TAG="release"
elif [ "${OSSIM_GIT_BRANCH}" == "dev" ] ; then
  export TAG="latest"
elif [ ! -z $OSSIM_GIT_BRANCH ] ; then
  export TAG="${OSSIM_GIT_BRANCH}"
fi


if [ -z $S3_DELIVERY_BUCKET ]; then
  export S3_DELIVERY_BUCKET="s3://o2-delivery/${OSSIM_GIT_BRANCH}"
fi

echo
echo OMAR_SCRIPT_DIR=$OMAR_SCRIPT_DIR
echo OSSIM_DEV_HOME=$OSSIM_DEV_HOME
echo DOCKER_REGISTRY_URI=$DOCKER_REGISTRY_URI
echo AWS_ACCOUNT_ID=$AWS_ACCOUNT_ID
echo AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
echo AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION
echo S3_DELIVERY_BUCKET=$S3_DELIVERY_BUCKET
echo

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
