<div class = "modal" id = "sourceSelectionDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog modal-lg">
 		<div class = "modal-content">
			<div class = "modal-header"><h4>Source Selection Table</h4></div>
			<div class = "modal-body">
				<div class = "row">
					<div class = "col-md-6">
						<select class = "form-control" id = "sensorModelSelect">
							<option value="" disabled selected>Select Sensor Model</option>
							<optgroup label = "Non-Rigorous">
								<option value = "model2">Model 1</option>
								<option value = "model2">Model 2</option>
							</optgroup>
							<optgroup label = "Rigorous">
								<option value = "model3">Model 3</option>
								<option value = "model4">Model 4</option>
							</optgroup>
						</select>
					</div>
					<div align = "center" class = "col-md-6">
						<div class = "col-md-6">
							Overall CE: <br>
							<span id = "overallCeSpan">-</span>
						</div>
						<div align = "center" class = "col-md-6">
							Overall LE: <br>
							<span id = "overallLeSpan">-</span>
						</div>
					</div>
				</div>
				<table class = "table table-condensed table-striped" id = "sourceSelectionTable"></table>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-primary" data-dismiss = "modal" id = "selectImagesButton">Select</button>
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
 			</div>
		</div>
	</div>
</div>

<g:javascript>
	$( "#sourceSelectionDialog" ).on( "hidden.bs.modal", function ( event ) { hideDialog( "sourceSelectionDialog" ); });
	$( "#sourceSelectionDialog" ).on( "shown.bs.modal", function ( event ) { displayDialog( "sourceSelectionDialog" ); });
</g:javascript>

<div class = "modal modal-xl" id = "tiePointSelectionDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog">
		<div class = "modal-content">
			<div class = "modal-header"><h4>Select Tie Points</h4></div>
			<div class = "modal-body">
                <div id = "tiePointMaps"></div>
			</div>
			<div class = "modal-footer">
				<button class = "btn btn-primary" onclick = "changeTiePointFrame( 'rewind' )" type = "button">
					<span class = "glyphicon glyphicon-step-backward"></span>
				</button>
				<button class = "btn btn-primary" onclick = "changeTiePointFrame( 'fastForward' )" type = "button">
					<span class = "glyphicon glyphicon-step-forward"></span>
				</button>
				<button type = "button" class = "btn btn-primary" onclick = addTiePoint()>Add Tie Point</button>
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
			</div>
		</div>
	</div>
</div>

<g:javascript>
	$( "#tiePointSelectionDialog" ).on( "shown.bs.modal", function( event ) {
  		var height = $( window ).height() - 200;
  		$( this ).find( ".modal-body" ).css( "max-height", height );
	});
</g:javascript>
