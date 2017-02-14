#!/bin/sh

./gradlew assemble
docker build -t geoscript-app:latest .
#docker run --rm -p 8080:8080 geoscript-app
docker tag geoscript-app:latest docker-registry-default.o2.radiantbluecloud.com/o2/geoscript-app:latest
oc login https://openshift-master.radiantbluecloud.com:8443
oc project omar-dev
docker login -p `oc whoami -t` -e unused -u unused docker-registry-default.o2.radiantbluecloud.com
docker push docker-registry-default.o2.radiantbluecloud.com/o2/geoscript-app
