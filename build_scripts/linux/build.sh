#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $SCRIPT_DIR/../../.. >/dev/null
export ROOT_DIR=$PWD
popd >/dev/null

. $SCRIPT_DIR/env.sh

if [ -z $OMAR_COMMON_PROPERTIES ] ; then
  echo "OMAR_COMMON_PROPERTIES is not defined and must point to the common-properties file"
  echo "The comon proeprties can be downloaded from https://github.com/ossimlabs/omar-common"
  exit 1
fi
#
# Compile the plugins first
#
for plugin in ${O2_PLUGINS[@]} ; do
   echo "BUILDING: $plugin ..."
   pushd $OMAR_DEV_HOME/plugins/$plugin
    ./gradlew install
   if [ $? -ne 0 ] ; then
       echo "BUILD ERROR: ${plugin} failed to build..."
       popd
       exit 1
   fi
   popd
done

for app in ${O2_APPS[@]} ; do
   echo "BUILDING: $app ..."
   pushd $OMAR_DEV_HOME/apps/$app
   if [ $app = "disk-cleanup" ]; then
      ./gradlew jar
   else
      ./gradlew assemble
   fi
   if [ $? -ne 0 ];then
       echo "BUILD ERROR: $app failed to build..."
       exit 1
   fi
   popd
done
