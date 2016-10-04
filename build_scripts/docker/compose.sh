#!/bin/bash

# Builds and runs docker stack for O2.

display_usage() {
  echo "Usage: ./compose.sh <local|o2|oc2s> <up|down> [sudo]"
}

if [ "$1" == "local" ]; then
  DOCKER_REGISTRY_URL="320588532383.dkr.ecr.us-east-1.amazonaws.com"
  DOCKER_HOST_URL="o2.radiantbluecloud.com"
  AWS_CREDENTIALS_PATH="/home/jenkins/.aws:/root/.aws"
elif [ "$1" == "o2" ]; then
  DOCKER_REGISTRY_URL="320588532383.dkr.ecr.us-east-1.amazonaws.com"
  DOCKER_HOST_URL="o2.radiantbluecloud.com"
  AWS_CREDENTIALS_PATH="/home/jenkins/.aws:/root/.aws"
elif [ "$1" == "o2" ]; then
  DOCKER_REGISTRY_URL="320588532383.dkr.ecr.us-east-1.amazonaws.com"
  DOCKER_HOST_URL="o2.radiantbluecloud.com"
  AWS_CREDENTIALS_PATH="/home/jenkins/.aws:/root/.aws"
else
  display_usage
  exit 1
fi

if [ "$2" == "up" ] || [ "$2" == "down" ]; then

 $3 docker-compose  $2 
 if [ $? -ne 0 ]; then
   echo "Ignoring errors..."
 fi
else
 display_usage
 exit 1
fi


