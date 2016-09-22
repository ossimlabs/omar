#!/bin/bash

string=$1
prefix="docker-compose-"
suffix=".yml"

file=$prefix$1$suffix

display_usage() {
  echo "Usage: ./compose.sh <env> <up|down> [sudo]"
  echo "Where env in:"
  for env in `ls $prefix*$suffix`; do
    string=$env
    env=${string#$prefix}
    env=${env%$suffix}
    echo "   ${env}"
  done
}

if [ -a $file ]; then

  if [ "$2" == "up" ] || [ "$2" == "down" ]; then

    # if [ "$1" == "local" ]; then
    #
    # echo "Running docker-compose $2 using $1 settings..."
    # docker-compose --file=docker-compose-$1.yml $2 #-d
    #
    # elif [ "$1" == "oc2s" ]; then
    #
    # echo "Running docker-compose $2 using $1 settings..."
    $3 docker-compose --file=docker-compose-$1.yml $2 #-d
    #
    # fi
    if [ $? -ne 0 ]; then
      echo "Ignoring errors..."
    fi
  else
    display_usage
    exit 1
  fi

else
  display_usage
  exit 1
fi
