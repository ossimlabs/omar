<div class = "modal" id = "summaryTableDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog">
 		<div class = "modal-content">
			<div class = "modal-header"><h4>Summary Table</h4></div>
			<div class = "modal-body">
				<table class = "table table-condensed table-striped" id = "timeLapseSummaryTable"></table>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
 			</div>
		</div>
	</div>
</div>

<g:javascript>
	$("#summaryTableDialog").on("hidden.bs.modal", function (event) { hideDialog("summaryTableDialog"); });
	$("#summaryTableDialog").on("shown.bs.modal", function (event) { displayDialog("summaryTableDialog"); });
</g:javascript>

<div class = "modal" id = "contextMenuDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog">
		<div class = "modal-content">
			<div class = "modal-header"><h4>Context Menu</h4></div>
			<div class = "modal-body">
				<div align = "center" class = "row"><b><i>You clicked here:</i></b></div>
				<div class = "row" id = "mouseClickDiv"></div>
				<hr>
				<div align = "center" class = "row"><b><i>Image Metadata:</i></b></div>
				<div class = "row" id = "imageMetadataDiv"></div>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
			</div>
		</div>
	</div>
</div>

<g:javascript>
	$("#contextMenuDialog").on("hidden.bs.modal", function (event) { hideDialog("contextMenuDialog"); });
	$("#contextMenuDialog").on("shown.bs.modal", function (event) { displayDialog("contextMenuDialog"); });
</g:javascript>
