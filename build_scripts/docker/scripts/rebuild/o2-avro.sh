#!/bin/bash
echo "Stopping o2-avro container..."
docker stop o2-avro
echo "Removing o2-avro container..."
docker rm o2-avro
echo "Removing o2-avro image..."
docker rmi ossimlabs/o2-avro
echo "Running o2-avro with docker-compose..."
docker-compose --file ../../docker-compose.yml run -d --service-ports --name o2-avro o2-avro
