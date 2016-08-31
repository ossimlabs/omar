#!/bin/bash
export OMAR_DEV_HOME=$ROOT_DIR/omar
export O2_APPS=( "omar-app" "sqs-app" "mensa-app" "avro-app" "download-app" "wfs-app" "wms-app" "wcs-app" "stager-app" "swipe-app" "superoverlay-app" "jpip-app wmts-app" )

if [ -z "$OMAR_INSTALL_PREFIX" ]; then
   export OMAR_INSTALL_PREFIX=$ROOT_DIR/install
fi

