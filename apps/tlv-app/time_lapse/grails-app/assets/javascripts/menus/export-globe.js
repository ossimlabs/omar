function exportScreenshotGlobe() {
	var canvas = tlv.globe.getCesiumScene().canvas;
	canvas.toBlob(function(blob) {
		var filename = "tlv_screenshot_" + new Date().generateFilename() + ".png";
		clientFileDownload(filename, blob);
	});
}
