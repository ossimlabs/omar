#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $SCRIPT_DIR/../../.. >/dev/null
export ROOT_DIR=$PWD
popd >/dev/null

. $SCRIPT_DIR/env.sh

for app in ${O2_APPS[@]} ; do
   echo "BUILDING: $app ..."
   pushd $OMAR_DEV_HOME/apps/$app
   if [ $app = "disk-cleanup" ]; then
      ./gradlew jar
   elif [ $app = "tlv-app" ]; then
      pushd time_lapse
      ./gradlew assemble
      popd
      mkdir -p build/libs
      for artifact in  `ls time_lapse/build/libs/*.jar` ; do
         mv $artifact build/libs
      done
   else
      ./gradlew assemble
   fi
   if [ $? -ne 0 ];then
       echo "BUILD ERROR: $x failed to build..."
       exit 1
   fi
   popd
done
