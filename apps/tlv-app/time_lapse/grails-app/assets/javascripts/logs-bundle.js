//= require jquery-2.2.0.min
//= require prototype


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

function exportLogsTable() {
	var csvData = [];

	var handleCommas = function(string) { return string.match(/,/g) ? '"' + string + '"' : string; }

	var table = $("body").find("table")[0];
	$.each(
		table.rows,
		function(i, x) {
			var values = [];
			$.each(
				x.cells, 
				function(j, y) {
					var value;
					if (i == 0) { 
						var cell = $(y).children()[0];
						value = $(cell).html(); 
					}
					else { value = $(y).html(); }
					values.push(handleCommas(value));
				}
			);
			csvData.push(values.join(","));
		}
	);

	// download
	var filename = "tlv_logs_" + new Date().generateFilename() + ".csv";
	var buffer = csvData.join("\n");
	var blob = new Blob([buffer], { "type": "text/csv;charset=utf8;" });
	clientFileDownload(filename, blob);
}
