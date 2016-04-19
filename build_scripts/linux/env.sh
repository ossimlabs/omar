#!/bin/bash
export OMAR_DEV_HOME=$ROOT_DIR/omar
export O2_APPS=( "omar-app" "wfs-app" "wms-app" "stager-app" "swipe-app" "superoverlay-app" "jpip-app wmts-app" )

if [ -z "$OMAR_INSTALL_PREFIX" ]; then
   export OMAR_INSTALL_PREFIX=$OMAR_DEV_HOME/omar-install
fi

