#!/bin/bash

if [ "$1" == "local" ] || [ "$1" == "aws" ]; then

  if [ "$2" == "up" ] || [ "$2" == "down" ]; then

    echo "Running docker-compose $2 using $1 settings..."
    docker-compose --file=docker-compose-$1.yml $2 #-d

  else

    echo "Error. Please use up or down as the second argument."

  fi

else

  echo "Error. Please use local or aws as the first argument."

fi
