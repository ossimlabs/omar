#!/bin/sh

APP_NAME=geoscript-app
APP_TAG=latest
DOCKER_REGISTRY_URL=docker-registry-default.o2.radiantbluecloud.com
REGISTRY_PROJECT_NAME=o2
OPENSHIFT_URL=https://openshift-master.radiantbluecloud.com:8443

./gradlew assemble
docker build -t $APP_NAME:$APP_TAG .
docker tag $APP_NAME:latest $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
oc login $OPENSHIFT_URL
#oc project omar-dev
docker login -p `oc whoami -t` -e unused -u unused docker-registry-default.o2.radiantbluecloud.com
docker push  $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
