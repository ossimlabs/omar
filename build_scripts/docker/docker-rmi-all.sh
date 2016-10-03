#!/bin/bash

# Locates script dir to find docker-common.sh
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

containers=($(docker ps -q))
for container in ${containers[@]} ; do
   echo "Stopping docker container ${container} "
   docker stop ${container}
done

for app in ${O2_APPS[@]} ; do
   getImageName $app $TAG
   exists=$( docker images | grep -c -e "$app[ ]\{2,\}${TAG}") 
   if [ $exists != "0" ]; then
     echo "Removing docker image ${imagename} "
     docker rmi -f ${imagename}
   else
     echo "Skipping missing ${imagename}."
   fi
done

echo; echo Remaining images:
docker images
echo
