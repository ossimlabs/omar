#!/bin/bash

echo "Running docker-compose up using aws settings..."
docker-compose --file=docker-compose-aws.yml up
