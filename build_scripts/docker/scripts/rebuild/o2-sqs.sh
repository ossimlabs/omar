#!/bin/bash
echo "Stopping o2-sqs container..."
docker stop o2-sqs
echo "Removing o2-sqs container..."
docker rm o2-sqs
echo "Removing o2-sqs image..."
docker rmi ossimlabs/o2-sqs
echo "Running o2-sqs with docker-compose..."
docker-compose --file ../../docker-compose-local.yml run -d --service-ports --name o2-sqs o2-sqs
