#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $SCRIPT_DIR/../.. >/dev/null
export OMAR_DEV_HOME=$PWD
popd >/dev/null

export OMAR_HOME="$OMAR_DEV_HOME/apps/omar-app"

if [ -z "$OSSIM_DEV_HOME" ]; then
   pushd $OMAR_DEV_HOME/.. > /dev/null
   export OSSIM_DEV_HOME=$PWD
   popd >/dev/null
fi
if [ -z "$OSSIM_INSTALL_PREFIX" ]; then
   export OSSIM_INSTALL_PREFIX=$OSSIM_DEV_HOME/install
fi

