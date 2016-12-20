function exportScreenshotMap() {
	tlv.map.once(
		"postcompose",
		function(event) {
			var canvas = event.context.canvas;
			canvas.toBlob(function(blob) {
				var filename = "tlv_screenshot_" + new Date().generateFilename() + ".png";
				clientFileDownload(filename, blob);
			});
		}
	);
	tlv.map.renderSync();
}
