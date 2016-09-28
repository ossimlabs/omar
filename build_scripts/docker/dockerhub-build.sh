#!/bin/bash
. docker-common.sh
for app in ${O2_APPS[@]} ; do
   echo "Building ${app} docker image"
   pushd ${app}
   docker rmi ossimlabs/${app}
   docker build -t ossimlabs/${app}:${TAG} .
   if [ $? -ne 0 ]; then
     echo; echo "ERROR: Building container ${app} with tag ${TAG}"
     popd
     exit 1
   fi
   docker push ossimlabs/${app}:${TAG}
   if [ $? -ne 0 ]; then
     echo; echo "ERROR: Pushing container ${app} with tag ${TAG}"
     popd
     exit 1
   fi
   popd
done
