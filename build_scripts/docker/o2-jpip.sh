#!/bin/bash
echo "Stopping o2-jpip container..."
docker stop o2-jpip
echo "Removing o2-jpip container..."
docker rm o2-jpip
echo "Removing o2-jpip image..."
docker rmi ossimlabs/o2-jpip
echo "Running o2-jpip with docker-compose..."
docker-compose run --service-ports --name o2-jpip o2-jpip
