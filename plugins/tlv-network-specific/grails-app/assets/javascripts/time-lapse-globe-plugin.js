var setupGlobePlugin = setupGlobe;
setupGlobe = function() {
	setupGlobePlugin();

	tlv.globe.getCesiumScene().terrainProvider = new Cesium.CesiumTerrainProvider({
		url : tlv.availableResources.terrainProvider,
		requestWaterMask: true
	});
}
