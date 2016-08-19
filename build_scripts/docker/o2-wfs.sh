#!/bin/bash
echo "Stopping o2-wfs container..."
docker stop o2-wfs
echo "Removing o2-wfs container..."
docker rm o2-wfs
echo "Removing o2-wfs image..."
docker rmi ossimlabs/o2-wfs
echo "Running o2-wfs with docker-compose..."
docker-compose run --service-ports --name o2-wfs o2-wfs
