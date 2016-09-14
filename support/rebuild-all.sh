#!/bin/sh

#for x in `find . -type d -depth 1`; do
for x in `find . -type d -depth 1 -name "omar*"`; do
	echo $x
	cd $x
	./gradlew assemble
	cd ..
done

docker images | grep omar | awk '{print $1}' | xargs docker rmi
