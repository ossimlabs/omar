#!/bin/sh

APP_NAME=service-proxy
APP_TAG=release
DOCKER_REGISTRY_URL=docker-registry-default.o2.radiantbluecloud.com
REGISTRY_PROJECT_NAME=o2
OPENSHIFT_URL=https://openshift-master.radiantbluecloud.com:8443

./gradlew assemble

docker build -t $APP_NAME:$APP_TAG .
docker tag $APP_NAME:latest $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
oc login -u $OPENSHIFT_USER -p $OPENSHIFT_PASSWORD $OPENSHIFT_URL
docker login -p `oc whoami -t` -e unused -u unused $DOCKER_REGISTRY_URL
docker push  $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
