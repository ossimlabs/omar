#!/bin/bash

echo "Running docker-compose down using aws settings..."
docker-compose --file=docker-compose-aws.yml down
