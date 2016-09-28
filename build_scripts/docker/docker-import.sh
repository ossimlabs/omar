#!/bin/bash

#=================================================================================
#
# This script copies O2 docker container images from an S3 bucket to the
# local machine, and loading these into the docker instance. The image TAR
# files are expected to be at the S3 bucket specified in the 
# $S3_DELIVERY_BUCKET environment variable. The images are loaded but the 
# containers are not launched.
#
#=================================================================================

# Assigns O2_APPS, TAG and functions:
. docker-common.sh

runCommand mkdir -p image_import
# runCommand rm -rf image_import/*.tgz
pushd image_import

for app in ${O2_APPS[@]} ; do
      
   # downloading the tar file from S3
   getTarFileName ${app} ${TAG}
   aws s3 cp $S3_DELIVERY_BUCKET/$tarfilename $tarfilename 
   if [ $? != 0 ] ; then
      echo "Skipping import of missing image <$tarfilename>."
   else
      # Run the import and verify image is available:
      echo "Importing docker image $imagename from $tarfilename"
      runCommand docker load -i $tarfilename

      getImageName ${app} ${TAG}
      exists=$(docker images | grep -c -e "$app[ ]\{2,\}${TAG}") 
      if [ $exists == "0" ]; then
         echo "WARNING: Image <$imagename> does not show in docker images list. "
         echo "That service will not be available."
      else
         echo "SUCCESS: Image <$imagename> successfully imported. "
      fi
   fi
done
popd

echo "Available Docker Images:"
docker images
echo

echo "Image TAR files in $PWD/image_import can be deleted."; echo
