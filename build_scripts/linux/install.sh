#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null
. $SCRIPT_DIR/env.sh

if [ ! -d $OSSIM_INSTALL_PREFIX/share/omar ]; then
   mkdir -p $OSSIM_INSTALL_PREFIX/share/omar
fi
cp $OMAR_HOME/build/libs/omar-app-*.war $OSSIM_INSTALL_PREFIX/share/omar
RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "INSTALL ERROR: Unable to copy $OMAR_HOME/build/libs/omar-app-*.war to location $OSSIM_INSTALL_PREFIX/share/omar"
else
    RETURN_CODE=0;
fi
cp $OMAR_HOME/build/libs/omar-app-*.jar $OSSIM_INSTALL_PREFIX/share/omar
RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "INSTALL ERROR: Unable to copy $OMAR_HOME/build/libs/omar-app-*.jar to location $OSSIM_INSTALL_PREFIX/share/omar"
else
    RETURN_CODE=0;
fi
