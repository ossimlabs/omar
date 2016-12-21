#!/bin/bash
export OMAR_DEV_HOME=$ROOT_DIR/omar
export O2_APPS=( "avro-app" "disk-cleanup" "download-app" "jpip-app" "mensa-app" "omar-app" "sqs-app" "stager-app" "superoverlay-app" "tlv-app" "wcs-app" "wfs-app" "wms-app" "wmts-app" )

if [ -z "$OMAR_INSTALL_PREFIX" ]; then
   export OMAR_INSTALL_PREFIX=$ROOT_DIR/install
fi
