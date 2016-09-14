#!/bin/sh

x=omar-zuul-server
echo $x
cd $x
./gradlew assemble
cd ..

docker images | grep $x | awk '{print $1}' | xargs docker rmi
