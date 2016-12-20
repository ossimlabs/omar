<button class = "btn btn-default navbar-btn" onclick = 'changeFrame("rewind"); $(this).blur();' title = "Rewind" type = "button">
	<span class = "glyphicon glyphicon-step-backward"></span>
</button>

<button class = "btn btn-default navbar-btn" onclick = 'playStopTimeLapse($(this).children()[0]); $(this).blur();' title = "Play/Stop" type = "button">
	<span class = "glyphicon glyphicon-play"></span>
</button>

<button class = "btn btn-default navbar-btn" onclick = 'changeFrame("fastForward"); $(this).blur();' title = "Fast Forward" type = "button">
	<span class = "glyphicon glyphicon-step-forward"></span>
</button>

<button class = "btn btn-default navbar-btn" onclick = 'buildSummaryTable();$("#summaryTableDialog").modal("show");' title = "Summary Table" type = "button">
	<span id = "tlvLayerCountSpan">0/0</span>
	<span class = "glyphicon glyphicon-list-alt"></span>
</button>
