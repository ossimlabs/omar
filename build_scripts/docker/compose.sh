#!/bin/bash

if [ "$1" == "local" ] || [ "$1" == "oc2s" ]; then

  if [ "$2" == "up" ] || [ "$2" == "down" ]; then

    if [ "$1" == "local" ]; then

    echo "Running docker-compose $2 using $1 settings..."
    docker-compose --file=docker-compose-$1.yml $2 #-d

    elif [ "$1" == "oc2s" ]; then

    echo "Running docker-compose $2 using $1 settings..."
    sudo docker-compose --file=docker-compose-$1.yml $2 #-d

    fi


  else

    echo "Usage: ./compose.sh < local | oc2s > < up | down >"

  fi

else

  echo "Usage: ./compose.sh < local | oc2s > < up | down >"

fi
