#!/bin/bash
#=================================================================================
#
# Performs a build of all O2 docker images. Previous O2 images are removed from 
# the local docker instance. The images are pushed to the docker hub registry
#
#=================================================================================

# Assigns O2_APPS, TAG and functions:

pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

export AWS_REGION=us-east-1
function createRepositories()
{
  local repositories=$1[@]
  local currentRepositories=`aws ecr describe-repositories --region ${AWS_REGION}`
  local a=("${!repositories}")
  for i in "${a[@]}" ; do
     repoCheck=`echo $currentRepositories | grep $i`
     if [ "$repoCheck" = "" ] ; then
        aws ecr create-repository --region us-east-1 --repository-name $i
        if [ $? -ne 0 ]; then
          echo "Unable to create repository $1"
          return $?
        fi
     fi
  done

  return 0
}

function deleteImage()
{
  local REPOSITORY=$1
  local TAG=$2
  local IMAGES=`aws ecr list-images --repository-name $REPOSITORY --region us-east-1`
  if [ $? -ne 0 ]; then
    echo "No repository found $REPOSITORY"
    return $?
  fi
  local tagCheck=`echo $IMAGES | grep $TAG`
  if [ "$tagCheck" != "" ] ; then
    aws ecr batch-delete-image --repository-name ${REPOSITORY} --image-ids imageTag=${TAG} --region ${AWS_REGION}
    if [ $? -ne 0 ]; then
      return $?
    fi
  fi
  return 0
}


createRepositories O2_APPS
#remove images
for app in ${O2_APPS[@]} ; do
#  for x in `docker images | grep "/${app}.*${TAG}\|/${app}.*none" | awk '{print $3}'`; do 
#    docker rmi -f $x; 
#  done
  x=`docker -q -f "dangling=true"`
  if [ "$x" != "" ] ; then
    docker rmi -f $x
  fi
  x=`docker images | grep "/${app}.*${TAG}\g|/${app}.*none" | awk '{print $3}'`
  if [ "${x}" != "" ] ; then
    docker rmi -f $x
  fi
done

for app in ${O2_APPS[@]} ; do
   echo "Building ${app} docker image"
   if [ "${app}" != "o2-db" ] ; then
     pushd ${app}
     getImageName ${app} ${TAG}
     cp Dockerfile Dockerfile.back
     sed -i -e "s/FROM.*ossimlabs.*o2-base/FROM ${DOCKER_REGISTRY_URI}\/o2-base\:${TAG}/" Dockerfile
     docker build  --no-cache -t ${imagename} .
          
     if [ $? -ne 0 ]; then
       echo; echo "ERROR: Building container ${app} with tag ${TAG}"
       popd
       exit 1
     fi
     mv Dockerfile.back Dockerfile

     deleteImage ${app} ${TAG}
     if [ $? -ne 0 ]; then
       echo; echo "ERROR: Deleting image ${app}:${TAG}"
       popd
       exit 1
     fi
     docker push ${imagename}
     if [ $? -ne 0 ]; then
       echo; echo "ERROR: Pushing container ${app} with tag ${TAG}"
       popd
       exit 1
     fi
     popd
   fi
done
