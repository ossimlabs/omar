var createImageLayerSourceOmar = createImageLayerSource;
createImageLayerSource = function(layer) {
	if (layer.library == "omar") {
			return new ol.source.TileWMS({
				crossOrigin: "anonymous",
				params: {
					BANDS: "default",
					BRIGHTNESS: 0,
					CONTRAST: 1,
					FORMAT: "image/png",
					IDENTIFIER: Math.floor(Math.random() * 1000000),
					INTERPOLATION: "bilinear",
					LAYERS: layer.indexId,
					SHARPEN_MODE: "none",
					STRETCH_MODE: "linear_auto_min_max",
					STRECTH_MODE_REGION: "viewport",
                                        TRANSPARENT: true,
                                        VERSION: "1.1.1"
				},
				url: tlv.availableResources.complete.omar.viewUrl
			});
	}
	else { return createImageLayerSourceOmar(layer); }
}
