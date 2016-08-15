#!/bin/sh
echo "Building o2-avro docker image"
pushd o2-avro
docker build -t ossimlabs/o2-avro:latest .
docker push ossimlabs/o2-avro:latest
popd
