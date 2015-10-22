#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
pushd $SCRIPT_DIR/../.. >/dev/null
export OMAR_DEV_HOME=$PWD
export OMAR_HOME=$OMAR_DEV_HOME/apps/omar-app
popd >/dev/null
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use groovy $GROOVY_VERSION
sdk use grails $GRAILS_VERSION
sdk use gradle $GRADLE_VERSION

pushd $OMAR_HOME >/dev/null
#gradle build
grails package
RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "BUILD ERROR: grails failed build..."
else
    RETURN_CODE=0;
fi

#
popd >/dev/null


exit $RETURN_CODE



