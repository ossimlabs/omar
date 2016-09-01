#!/bin/bash

echo "Running docker-compose down using local settings..."
docker-compose --file=docker-compose-local.yml down
