#!/bin/bash

echo "Running docker-compose $2 using $1 settings..."
docker-compose --file=docker-compose-$1.yml $2 #-d

# echo "Running wait for it..."
# ./wait-for-it.sh --wget http://192.168.99.100/o2-wms/api --timeout=660 --strict -- ./docker-compose-local-wms.sh
