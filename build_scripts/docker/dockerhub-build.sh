#!/bin/bash

#=================================================================================
#
# Performs a build of all O2 docker images. Previous O2 images are removed from 
# the local docker instance. The images are pushed to the docker hub registry
#
#=================================================================================

# Assigns O2_APPS, TAG and functions:
. docker-common.sh

for app in ${O2_APPS[@]} ; do
   echo "Building ${app} docker image"
   pushd ${app}
   getImageName ${app} ${TAG}
   docker rmi ${imagename}
   docker build -t ${imagename} .
   if [ $? -ne 0 ]; then
     echo; echo "ERROR: Building container ${app} with tag ${TAG}"
     popd
     exit 1
   fi
   docker push $imagename
   if [ $? -ne 0 ]; then
     echo; echo "ERROR: Pushing container ${app} with tag ${TAG}"
     popd
     exit 1
   fi
   popd
done
