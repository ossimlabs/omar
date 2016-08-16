#!/bin/sh
export O2_APPS=( "o2-avro" "o2-db" "o2-download" "o2-jpip" "o2-jpip-server" "o2-omar" "o2-sqs" "o2-stager" "o2-superoverlay" "o2-swipe" "o2-wfs" "o2-wms" "o2-wmts")
export tag="latest"
for app in ${O2_APPS[@]} ; do 
   echo "Building ${app} docker image"
   pushd ${app}
   docker rmi ossimlabs/${app}
   docker build -t ossimlabs/${app}:${tag} .
   docker push ossimlabs/${app}:${tag}
   popd
done
