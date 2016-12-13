#!/bin/bash

#=================================================================================
#
# This script serializes the O2 docker container images to a TAR archive and
# thewn uploads them to an S3 bucket. The images must be available on the local
# machine's docker instance. The destination S3 bucket is specified on the 
# command line or in the $S3_DELIVERY_BUCKET environment variable. Upon 
# successful upload, the local copies of the TAR files are deleted from the 
# local machine.
#
# Usage: docker-export [-a] [s3://<path_to_docker_dir>]
#
# -a   If specified, non O2 images (centos, twofishes) will be saved and
#      uploaded along with all O2 images.
#=================================================================================

# Uncomment following line to debug script line by line:
#set -x; trap read debug

if [ "$1" == "-a" ]; then
  do_all=true
  s3_bucket=$2
else
  s3_bucket=$1
fi

# Locates script dir to find docker-common.sh
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

# Assigns O2_APPS and TAG and functions:
. $SCRIPT_DIR/docker-common.sh

if [ -z ${s3_bucket} ]; then
  s3_bucket=${S3_DELIVERY_BUCKET}
fi

# Don't forget to append the docker-specific subdir!!
s3_bucket+="/docker"
echo "Using S3 bucket=<${s3_bucket}>"

mkdir -p image_export 
rm -rf image_export/*
pushd image_export

# Add the large static packages if -a option supplied"
if [ ${do_all} ]; then
  O2_APPS+=( centos twofishes )
fi

for app in ${O2_APPS[@]} ; do
   
   # Pull the built images from the docker registry if not present on host:
   getImageName ${app} ${TAG}
   exists=$( docker images | grep -c -e "${app}[ ]\{2,\}${TAG}") 
   if [ ${exists} == "0" ]; then
      runCommand docker pull ${imagename}
   fi
   
   exists=$( docker images | grep -c -e "${app}[ ]\{2,\}${TAG}") 
   if [ ${exists} != "0" ]; then
      
      # Export the image to local tar file
      tarfilename="${app}.tgz"
      echo "Exporting docker image ${imagename} to ${tarfilename}"
      runCommand docker save ${imagename} \| gzip \> ${tarfilename}
   
      # upload the tar file to S3
      echo "Uploading ${tarfilename} to ${s3_bucket}"
      runCommand aws s3 sync . ${s3_bucket} --exclude \"*\" --include ${tarfilename}
      echo "SUCCESS: Image <${imagename}> successfully exported and archived. "
      
      # Whack the local tar file:
      runCommand rm ${tarfilename}
      
   else
      echo "Skipping export of missing image ${imagename}."
   fi
done

# For convenience, upload the import and compose script to the same S3 bucket:
pushd $SCRIPT_DIR
runCommand tar cvfz docker-scripts.tgz docker-common.sh docker-import.sh docker-compose-no-build.yml docker-rmi-all.sh compose.sh
runCommand aws s3 cp docker-scripts.tgz ${s3_bucket}/ 
popd

# delete the temporary tar files:
popd
#rm -rf image_export

exit 0


