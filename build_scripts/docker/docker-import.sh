#!/bin/bash

#=================================================================================
#
# This script loads O2 docker container images from the local filesystem
# directory supplied (or S3 bucket) into the running docker instance. 
# The images are loaded but the containers are not launched.
#
# Usage: docker-import.sh [<path_to_tarfiles>]
#
# <path_to_tarfies> can be a directory on local filesystem or an S3 bucket
# specifed as "s3://...". If no arg is present, it assumes the S3 bucket 
# specified by the environment variable $S3_DELIVERY_BUCKET.
#
#=================================================================================

local_dir=$1
# Assigns O2_APPS, TAG and functions:
. docker-common.sh

if [ -z ${local_dir} ]; then
  s3_bucket=${S3_DELIVERY_BUCKET}
  tarfilepath=image_import
elif [[ ${local_dir} == *"s3://"* ]]; then
  s3_bucket=${local_dir}
  tarfilepath=image_import
else
  s3_bucket=""
  tarfilepath=${local_dir}  
  echo "Accessing image archives from local filesystem at <${tarfilepath}>." 
fi

# If downloading from S3, create local directory as workspace:
if [ ${s3_bucket} ]; then
   echo "Accessing S3 bucket at <${s3_bucket}> for image archives" 
   runCommand mkdir -p image_import
   # runCommand rm -rf image_import/*.tgz
fi

# TODO: Need to assign array var here to represent full collection of tarfiles in
# archive bucket/dir, not just o2-*
for app in ${O2_APPS[@]} ; do
   
   getTarFileName ${app} ${TAG}
   if [ ${s3_bucket} ]; then
      # downloading the tar file from S3 to local FS
      aws s3 sync ${s3_bucket}/${tarfilename} ${tarfilepath}/${tarfilename}
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
