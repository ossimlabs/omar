var setupGlobePlugin = setupGlobe;
setupGlobe = function() {
	setupGlobePlugin();

	tlv.globe.getCesiumScene().terrainProvider = new Cesium.CesiumTerrainProvider({
		url : "//assets.agi.com/stk-terrain/world",
		requestWaterMask: true
	});
}
