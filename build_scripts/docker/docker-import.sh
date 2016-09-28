#!/bin/bash

function runCommand() 
{
  $1
  if [ $? != 0 ] ; then 
    echo "ERROR: Failed while executing command: <$1>."
    echo; exit 1;
  fi
}

if [ -z $S3_DELIVERY_BUCKET ]; then
  S3_DELIVERY_BUCKET="s3://o2-delivery"
  echo "WARNING: No URL specified for S3 delivery bucket. Defaulting S3_DELIVERY_BUCKET = <$S3_DELIVERY_BUCKET>"
  echo;
fi

# Assigns O2_APPS and TAG:
. docker-common.sh

runCommand "mkdir -p image_import"
runCommand "rm -rf image_import/*.tar"
pushd image_import

for app in ${O2_APPS[@]} ; do
      
   # downloading the tar file from S3
   tarfile="${app}-${TAG}.tar"
   aws s3 cp $S3_DELIVERY_BUCKET/$tarfile $tarfile 
   if [ $? != 0 ] ; then    else
      echo "Skipping import of missing image <${tarfile}>."
   else
      # Run the import and verify image is available:
      echo "Importing docker image ${imagename} from $tarfile"
      runCommand "docker load -i $tarfile"

   imagename="ossimlabs/${app}:${TAG}"
   exists=$(docker images | grep -c -e "$app[ ]\{2,\}${TAG}") 
   if [ $exists == "0" ]; then
      echo "WARNING: Image <${imagename}> does not show in docker images list. "
      echo "That service will not be available."
   else
      echo "SUCCESS: Image <${imagename}> successfully imported. "
   fi
   
done
popd
