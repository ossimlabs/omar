<div class = "modal" id = "viewDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog">
		<div class = "modal-content">
			<div class = "modal-header"><h4>View</h4></div>
			<div class = "modal-body">
				<div class = "form-group">
					<label>Dimensions:</label>
					<select class = "form-control" id = "dimensionsSelect" onchange = "dimensionToggle(); $('#viewDialog').modal('hide');">
						<option value = 2>2D</option>
						<option value = 3>3D</option>
					</select>

					<label>Swipe</label>
					<select class = "form-control" id = "swipeSelect" onchange = "swipeToggle(); $('#viewDialog').modal('hide');">
						<option value = "off">OFF</option>
						<option value = "on">ON</option>
					</select>
				</div>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
			</div>
		</div>
	</div>
</div>

<g:javascript>
	$("#viewDialog").on("hidden.bs.modal", function (event) { hideDialog("viewDialog"); });
	$("#viewDialog").on("shown.bs.modal", function (event) { displayDialog("viewDialog"); });
</g:javascript>
