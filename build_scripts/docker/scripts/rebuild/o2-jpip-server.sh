#!/bin/bash
echo "Stopping o2-jpip-server container..."
docker stop o2-jpip-server
echo "Removing o2-jpip-server container..."
docker rm o2-jpip-server
echo "Removing o2-jpip-server image..."
docker rmi ossimlabs/o2-jpip-server
echo "Running o2-jpip-server with docker-compose..."
docker-compose --file ../../docker-compose.yml run -d --service-ports --name o2-jpip-server o2-jpip-server
