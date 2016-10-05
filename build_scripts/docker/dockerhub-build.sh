#!/bin/bash

#=================================================================================
#
# Performs a build of all O2 docker images. Previous O2 images are removed from 
# the local docker instance. The images are pushed to the docker hub registry
#
#=================================================================================

pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

for app in ${O2_APPS[@]} ; do
   echo "Building ${app} docker image"
   if [ "${app}" ne "os-db" ] ; then
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
   fi
done
