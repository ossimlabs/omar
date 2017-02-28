#!/bin/sh

APP_NAME=service-proxy
APP_TAG=latest

DOCKER_REGISTRY_URL=docker-registry-default.o2.radiantbluecloud.com
REGISTRY_PROJECT_NAME=o2
OPENSHIFT_URL=https://openshift-master.radiantbluecloud.com:8443

./gradlew assemble
sed -e s/REGISTRY_URI/$DOCKER_REGISTRY_URL/g -e s/PROJECT_PLACEHOLDER/$REGISTRY_PROJECT_NAME/g -e s/TAG_PLACEHOLDER/$APP_TAG/g Dockerfile > Dockerfile.tmp
docker build -f Dockerfile.tmp -t $APP_NAME:$APP_TAG .
rm -f Dockerfile.tmp
docker tag $APP_NAME:latest $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
oc login -u $OPENSHIFT_USERNAME -p $OPENSHIFT_PASSWORD $OPENSHIFT_URL
docker login -p `oc whoami -t` -e unused -u unused $DOCKER_REGISTRY_URL
docker push  $DOCKER_REGISTRY_URL/$REGISTRY_PROJECT_NAME/$APP_NAME:$APP_TAG
