var createImageLayerSourceO2 = createImageLayerSource;
createImageLayerSource = function( layer ) {
	if ( layer.library == "o2" ) {
			return new ol.source.TileWMS({
				crossOrigin: "anonymous",
				params: {
					FILTER: "in(" + layer.metadata.id + ")",
					FORMAT: "image/png",
					IDENTIFIER: Math.floor(Math.random() * 1000000),
					LAYERS: "omar:raster_entry",
					STYLES: JSON.stringify({
						bands: layer.bands || "default",
						brightness: layer.brightness || 0,
						contrast: layer.contrast || 1,
						histOp: layer.histOp || "auto-minmax",
						resamplerFilter: layer.resamplerFilter || "bilinear",
						sharpenMode: layer.sharpenMode || "none"
					}),
                    TRANSPARENT: true,
                    VERSION: "1.1.1"
				},
				url: tlv.availableResources.complete.o2.viewUrl
			});
	}
	else { return createImageLayerSourceO2( layer ); }
}
