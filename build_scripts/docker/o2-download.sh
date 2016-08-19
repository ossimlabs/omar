#!/bin/bash
echo "Stopping o2-download container..."
docker stop o2-download
echo "Removing o2-download container..."
docker rm o2-download
echo "Removing o2-download image..."
docker rmi ossimlabs/o2-download
echo "Running o2-download with docker-compose..."
docker-compose run --service-ports --name o2-download o2-download
