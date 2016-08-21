#!/bin/bash
echo "Stopping o2-omar container..."
docker stop o2-omar
echo "Removing o2-omar container..."
docker rm o2-omar
echo "Removing o2-omar image..."
docker rmi ossimlabs/o2-omar
echo "Running o2-omar with docker-compose..."
docker-compose --file ../../docker-compose.yml run -d --service-ports --name o2-omar o2-omar
