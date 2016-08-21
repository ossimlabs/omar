cs#!/bin/bash
echo "Stopping o2-wcs container..."
docker stop o2-wcs
echo "Removing o2-wcs container..."
docker rm o2-wcs
echo "Removing o2-wcs image..."
docker rmi ossimlabs/o2-wcs
echo "Running o2-wcs with docker-compose..."
docker-compose --file ../../docker-compose.yml run -d --service-ports --name o2-wcs o2-wcs
