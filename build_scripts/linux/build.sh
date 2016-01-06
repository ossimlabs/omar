#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $OMAR_HOME >/dev/null

./gradlew assemble

RETURN_CODE=$?
if [ $RETURN_CODE -ne 0 ];then
    echo "BUILD ERROR: omar-app failed build..."
else
    RETURN_CODE=0;
fi

#
popd >/dev/null


exit $RETURN_CODE


