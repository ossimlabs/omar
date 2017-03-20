var setupGlobePlugin = setupGlobe;
setupGlobe = function() {
	setupGlobePlugin();

	if ( tlv.availableResources.terrainProvider ) {
		tlv.globe.getCesiumScene().terrainProvider = new Cesium.CesiumTerrainProvider({
			url: tlv.availableResources.terrainProvider,
			requestWaterMask: true
		});
	}
}
