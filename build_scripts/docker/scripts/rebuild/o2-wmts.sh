#!/bin/bash
echo "Stopping o2-wmts container..."
docker stop o2-wmts
echo "Removing o2-wmts container..."
docker rm o2-wmts
echo "Removing o2-wmts image..."
docker rmi ossimlabs/o2-wmts
echo "Running o2-wmts with docker-compose..."
docker-compose --file ../../docker-compose-local.yml run -d --service-ports --name o2-wmts o2-wmts
