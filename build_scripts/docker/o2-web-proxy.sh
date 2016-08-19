#!/bin/bash
echo "Stopping o2-web-proxy container..."
docker stop o2-web-proxy
echo "Removing o2-web-proxy container..."
docker rm o2-web-proxy
echo "Removing o2-web-proxy image..."
docker rmi ossimlabs/o2-web-proxy
echo "Running o2-web-proxy with docker-compose..."
docker-compose run --service-ports --name o2-web-proxy o2-web-proxy
