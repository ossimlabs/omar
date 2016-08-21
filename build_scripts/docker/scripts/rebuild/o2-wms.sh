#!/bin/bash
echo "Stopping o2-wms container..."
docker stop o2-wms
echo "Removing o2-wms container..."
docker rm o2-wms
echo "Removing o2-wms image..."
docker rmi ossimlabs/o2-wms
echo "Running o2-wms with docker-compose..."
docker-compose --file ../../docker-compose.yml run -d --service-ports --name o2-wms o2-wms
