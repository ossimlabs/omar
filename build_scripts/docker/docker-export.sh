#!/bin/bash

#=================================================================================
#
# This script serializes the O2 docker container images to a TAR archive and
# thewn uploads them to an S3 bucket. The images must be available on the local
# machine's docker instance. The destination S3 bucket is specified in the 
# $S3_DELIVERY_BUCKET environment variable. Upon successful upload, the local
# copies of the TAR files are deleted from the local machine.
#
#=================================================================================

# Assigns O2_APPS and TAG and functions:
. docker-common.sh

mkdir -p image_export 
rm -rf image_export/*.tar
pushd image_export

for app in ${O2_APPS[@]} ; do
   getImageName ${app} ${TAG}
   exists=$( docker images | grep -c -e "$app[ ]\{2,\}${TAG}") 
   
   if [ $exists != "0" ]; then
      
      # Export the image to local tar file
      getTarFileName ${app} ${TAG}
      echo "Exporting docker image $imagename to $tarfilename"
      runCommand docker save $imagename \| gzip \> $tarfilename
   
      # upload the tar file to S3
      echo "Uploading $tarfile to $S3_DELIVERY_BUCKET"
      runCommand aws s3 cp $tarfile $S3_DELIVERY_BUCKET/
      echo "SUCCESS: Image <$imagename> successfully exported and archived. "
      
      # Whack the local tar file:
      runCommand rm $tarfilename
      
   else
      echo "Skipping export of missing image $imagename."
   fi
done
popd
