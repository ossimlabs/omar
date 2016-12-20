var createImageLayerSourceO2 = createImageLayerSource;
createImageLayerSource = function(layer) {
	if (layer.library == "o2") {
			return new ol.source.TileWMS({
				crossOrigin: "anonymous",
				params: {
					BANDS: "default",
					BRIGHTNESS: 0,
					CONTRAST: 1,
					FILTER: "index_id LIKE '" + layer.indexId + "'",
					FORMAT: "image/png",
					IDENTIFIER: Math.floor(Math.random() * 1000000),
					INTERPOLATION: "bilinear",
					LAYERS: "omar:raster_entry",
					SHARPEN_MODE: "none",
					STRETCH_MODE: "linear_auto_min_max",
					STRECTH_MODE_REGION: "viewport",
                    TRANSPARENT: true,
                    VERSION: "1.1.1"
				},
				url: tlv.availableResources.complete.o2.viewUrl
			});
	}
	else { return createImageLayerSourceO2(layer); }
}
