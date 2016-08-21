#!/bin/bash

if [ "$1" != "" ]; then

  # Set the Docker IP address that will be used in
  # our compose file using the argument.
  export DOCKER_HOST_IP=$1
  echo "Running docker-compose using IP = ${DOCKER_HOST_IP}"

  docker-compose up

else

  # Set the Docker IP address that will be used in
  # our compose file using the default address.
  export DOCKER_HOST_IP='192.168.99.100'
  echo "Running docker-compose using IP = ${DOCKER_HOST_IP}"

  docker-compose up

fi
