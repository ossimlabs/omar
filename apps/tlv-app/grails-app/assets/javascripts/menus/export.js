function clientFileDownload(filename, blob) {
	var link = document.createElement("a");
	if (link.download !== undefined) { // feature detection
		$(link).attr("href", window.URL.createObjectURL(blob));
		$(link).attr("download", filename);
		$("body").append(link);
		link.click();
	}
	else { displayErrorDialog("This browser doesn't support client-side downloading, :("); }
	link.remove();
}

function exportMetadata() {
	var csvData = [];

	// gather all the keys
	var keys = [];
	$.each(
		tlv.layers,
		function (i, x) {
			$.each(x.metadata, function(j, y) { keys.push(j); });
		}
	);
	// sort and deal with commas
	keys = keys.unique().sort().map(function(element) { return element.match(/,/g) ? '"' + element + '"' : element });
	// csv headers
	csvData.push(keys.join(","));

	// gather the metadata
	$.each(
		tlv.layers,
		function(i, x) {
			var values = [];
			$.each(
				keys,
				function(j, y) {
					var value = x.metadata[y] || "";

					// maintain formatting for objects
					if (typeof value === "object") { value = JSON.stringify(value); }

					// handle commas
					values.push(value.toString().match(/,/g) ? '"' + value + '"' : value);
				}
			);
			csvData.push(values.join(","));
		}
	);

	// download
	var filename = "tlv_metadata_" + new Date().generateFilename() + ".csv";
	var buffer = csvData.join("\n");
	var blob = new Blob([buffer], { "type": "text/csv;charset=utf8;" });
	clientFileDownload(filename, blob);
}

function exportScreenshot() {
	if (getCurrentDimension() == 2) { exportScreenshotMap(); }
	else { exportScreenshotGlobe(); }
}
