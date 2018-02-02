#!/bin/bash
pushd `dirname ${BASH_SOURCE[0]}` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $SCRIPT_DIR/../../.. >/dev/null
export ROOT_DIR=$PWD
popd >/dev/null

. $SCRIPT_DIR/env.sh

pushd $OMAR_DEV_HOME
mkdocs build -d omardocs
if [ $? -ne 0 ];then
    echo "BUILD DOCS ERROR: failed to build docs using mkdocs..."
    exit 1
fi 

tar cvfz omardocs.tgz omardocs
if [ $? -ne 0 ];then
    echo "BUILD DOCS ERROR: failed to zip up the docs..."
    exit 1
fi 
