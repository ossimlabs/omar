#!/bin/bash
. docker-common.sh
for app in ${O2_APPS[@]} ; do
   image=ossimlabs/${app}:${TAG}
   echo "Removing docker image ${image} "
   docker rmi -f ${image}
done
