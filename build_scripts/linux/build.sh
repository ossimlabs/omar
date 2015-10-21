#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
pushd $SCRIPT_DIR/../.. >/dev/null
export OMAR_DEV_HOME=$PWD
export OMAR_HOME=$OMAR_DEV_HOME/apps/omar
popd >/dev/null
source "$HOME/.sdkman/bin/sdkman-init.sh"

pushd $OMAR_HOME >/dev/null

#
popd >/dev/null



