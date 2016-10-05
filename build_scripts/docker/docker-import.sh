#!/bin/bash

#=================================================================================
#
# This script loads O2 docker container images from the local filesystem
# directory supplied (or S3 bucket) into the running docker instance. 
# The images are loaded but the containers are not launched.
#
# Usage: docker-import.sh [-a] [<path_to_tarfiles>]
#
# <path_to_tarfies> can be a directory on local filesystem or an S3 bucket
#      specifed as "s3://...". If no arg is present, it assumes the S3 bucket 
#      specified by the environment variable $S3_DELIVERY_BUCKET.
#
# -a   If specified, non O2 images (centos, twofishes) will be downloaded and
#      imported into docker as well as all O2 images.
#
#=================================================================================

# Uncomment following line to debug script line by line:
#set -x; trap read debug

if [ "$1" == "-a" ]; then
  do_all=true
  arg_path=$2
else
  arg_path=$1
fi

# Locates script dir to find docker-common.sh
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

if [ -z ${arg_path} ]; then
  s3_bucket=${S3_DELIVERY_BUCKET}/docker
  tarfilepath=image_import
elif [[ ${arg_path} == "s3://"* ]]; then
  s3_bucket=${arg_path}/docker
  tarfilepath=image_import
else
  s3_bucket=""
  tarfilepath=${arg_path}  
  echo "Accessing image archives from local filesystem at <${tarfilepath}>." 
fi

# If downloading from S3, create local directory as workspace:
if [ ${s3_bucket} ]; then
   echo "Accessing S3 bucket at <${s3_bucket}> for image archives" 
   runCommand mkdir -p image_import
   # runCommand rm -rf image_import/*.tgz
fi

# Add the large static packages if -a option supplied"
if [ ${do_all} ]; then
  O2_APPS+=( centos twofishes )
fi

for app in ${O2_APPS[@]} ; do
   
   tarfilename="${app}.tgz"
   if [ ${s3_bucket} ]; then
      # downloading the tar file from S3 to local FS
      aws s3 sync ${s3_bucket} ${tarfilepath} --exclude \"*\" --include ${tarfilename}
   fi

   # Check for the existence of the tar file on FS:
    if [ -z $(find ${tarfilepath} -name ${tarfilename}) ] ; then
      echo "Skipping import of missing image <${tarfilename}>."
   else
      # Run the import and verify image is available:
      echo "Importing docker image from ${tarfilepath}/${tarfilename}"
      runCommand docker load -i ${tarfilepath}/${tarfilename}

      getImageName ${app} ${TAG}
      exists=$(docker images | grep -c -e "${app}[ ]\{2,\}${TAG}") 
      if [ $exists == "0" ]; then
         echo "WARNING: Image <${imagename}> does not show in docker images list. "
         echo "That service will not be available."
      else
         echo "SUCCESS: Image <${imagename}> successfully imported. "
      fi
   fi
done


echo "Available Docker Images:"
docker images
echo

echo "Image TAR files in $PWD/image_import can be deleted."; echo
