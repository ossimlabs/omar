#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
pushd $SCRIPT_DIR/../.. >/dev/null
export OMAR_DEV_HOME=$PWD
pushd $OMAR_DEV_HOME/.. >/dev/null
export OSSIM_DEV_HOME=$PWD
export OSSIM_HOME=$OSSIM_DEV_HOME/ossim
echo "@@@@@ OMAR_DEV_HOME=$OMAR_DEV_HOME"
echo "@@@@@ OSSIM_DEV_HOME=$OSSIM_DEV_HOME"
echo "@@@@@ OSSIM_HOME=$OSSIM_HOME"
popd >/dev/null
popd >/dev/null

#
popd >/dev/null



