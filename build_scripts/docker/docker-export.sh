#!/bin/bash

function runCommand() 
{
  $*
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

mkdir -p image_export 
rm -rf image_export/*.tar
pushd image_export

for app in ${O2_APPS[@]} ; do
   imagename="ossimlabs/${app}:${TAG}"
   exists=$(sudo docker images | grep -c -e "$app[ ]\{2,\}${TAG}") 
   
   if [ $exists != "0" ]; then
      
      # Export the image to local tar file
      tarfile="${app}-${TAG}.tgz"
      echo "Exporting docker image ${imagename} to $tarfile"
      runCommand "docker save ${imagename} \| gzip \> $tarfile"
   
      # upload the tar file to S3
      echo "Uploading $tarfile to $S3_DELIVERY_BUCKET"
      runCommand "aws s3 cp $tarfile $S3_DELIVERY_BUCKET/"
      echo "SUCCESS: Image <${imagename}> successfully exported and archived. "
      
      # Whack the local tar file:
      runCommand "rm $tarfile"
      
   else
      echo "Skipping export of missing image ${imagename}."
   fi
done
popd
