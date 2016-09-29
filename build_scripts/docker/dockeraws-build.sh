#!/bin/bash
#=================================================================================
#
# Performs a build of all O2 docker images. Previous O2 images are removed from 
# the local docker instance. The images are pushed to the docker hub registry
#
#=================================================================================

# Assigns O2_APPS, TAG and functions:
export DOCKER_REGISTRY_URI="320588532383.dkr.ecr.us-east-1.amazonaws.com"

. docker-common.sh


function createRepositories()
{
    repositories=$1[@]
    currentRepositories=`aws ecr describe-repositories --region us-east-1`
    a=("${!repositories}")
    for i in "${a[@]}" ; do
       repoCheck=`echo $currentRepositories | grep $i`
       if [ "$repoCheck" = "" ] ; then
         aws ecr create-repository --region us-east-1 --repository-name $i
       fi
    done

}

createRepositories O2_APPS

# Create login credentials for docker
eval `aws ecr get-login --region us-east-1`

for app in ${O2_APPS[@]} ; do
   echo "Building ${app} docker image"
   pushd ${app}
   getImageName ${app} ${TAG}
   docker rmi ${imagename}
   docker build --build-arg DOCKER_REGISTRY_URI=$DOCKER_REGISTRY_URI -t ${imagename} .
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