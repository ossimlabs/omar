#!/bin/bash
export OMAR_DEV_HOME=$ROOT_DIR/omar
export O2_APPS=( "avro-app" "disk-cleanup" "download-app" "jpip-app" "mensa-app" "omar-app" "sqs-app" "stager-app" "superoverlay-app" "tlv-app" "wcs-app" "wfs-app" "wms-app" "wmts-app" )
export O2_PLUGINS=( "omar-hibernate-spatial" "omar-core" "omar-download" "omar-openlayers" "omar-oms" "omar-jpip" "omar-mensa" "omar-geoscript" "omar-wms" "omar-wcs" "omar-wfs" "omar-wmts" "omar-ingest-metrics" "omar-avro" "omar-stager" "omar-raster" "omar-video" "omar-superoverlay" "omar-opir" "tlv-network-specific" "three-disa")

if [ -z "$OMAR_INSTALL_PREFIX" ]; then
   export OMAR_INSTALL_PREFIX=$ROOT_DIR/install
fi
