#!/bin/bash

echo "Running docker-compose $1 using local settings..."
docker-compose --file=docker-compose-local.yml $1 #-d



# echo "Running wait for it..."
# ./wait-for-it.sh --wget http://192.168.99.100/o2-wms/api --timeout=660 --strict -- ./docker-compose-local-wms.sh
