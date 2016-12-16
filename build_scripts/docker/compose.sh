#!/bin/bash

# Builds and runs docker stack for O2.

display_usage() {
  echo
  echo "Usage: ./compose.sh <env> <action> [sudo]"
  echo
  echo "  <env>    = local|o2|oc2s or any suffix <X> to reference docker-compose-<X>.yml"
  echo "  <action> = up|down"
  echo
}

pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

# TODO Need to differentiate by <env>
if [ "$1" == "local" ]; then
  export DOCKER_COMPOSE_FILE="docker-compose-build.yml"
  export DOCKER_HOST_URL="localhost"
elif [ "$1" == "o2" ]; then
  export DOCKER_COMPOSE_FILE="docker-compose-no-build.yml"
  export DOCKER_HOST_URL="o2.radiantbluecloud.com"
elif [ "$1" == "oc2s" ]; then
  export DOCKER_COMPOSE_FILE="docker-compose-no-build.yml"
  export DOCKER_HOST_URL="oc2s-docker-test-01.rbtcloud.com"
elif [ -n $1 ]
  export DOCKER_COMPOSE_FILE="docker-compose-${1}.yml"
  if [ -z $DOCKER_HOST_URL ]; then
    export DOCKER_HOST_URL="localhost"
  fi
fi

if [ ! -r $DOCKER_COMPOSE_FILE ]; then
  echo "Cannot read the configuration file at <$DOCKER_COMPOSE_FILE>. Aborting."; echo
  display_usage
  exit 1
fi

if [ "$2" == "up" ] || [ "$2" == "down" ]; then
  $3 docker-compose -f $DOCKER_COMPOSE_FILE $2 
  if [ $? -ne 0 ]; then
    echo "Ignoring errors..."
  fi
else
  display_usage
  exit 1
fi

exit 0


