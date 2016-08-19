#!/bin/bash
echo "Stopping o2-superoverlay container..."
docker stop o2-superoverlay
echo "Removing o2-superoverlay container..."
docker rm o2-superoverlay
echo "Removing o2-superoverlay image..."
docker rmi ossimlabs/o2-superoverlay
echo "Running o2-superoverlay with docker-compose..."
docker-compose run --service-ports --name o2-superoverlay o2-superoverlay
