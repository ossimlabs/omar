<div class = "modal" id = "jobNameDialog" role = "dialog" style = "z-index: 2147483647" tabindex = "-1">
	<div class = "modal-dialog">
		<div class = "modal-content">
			<div class = "modal-header"><h4>Enter Job Name</h4></div>
			<div class = "modal-body">
                <input class = "form-control" id = "jobNameInput" type = "text">
			</div>
			<div class = "modal-footer">
				<button class = "btn btn-primary" data-dismiss = "modal" onclick = submitJob() type = "button">Submit</button>
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
			</div>
		</div>
	</div>
</div>

<g:javascript>
	$( "#jobNameDialog" ).on( "hidden.bs.modal", function ( event ) { hideDialog( "jobNameDialog" ); });
	$( "#jobNameDialog" ).on( "shown.bs.modal", function ( event ) { displayDialog( "jobNameDialog" ); });
</g:javascript>

<div class = "modal" id = "jobSearchDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog modal-lg">
 		<div class = "modal-content">
			<div class = "modal-header"><h4>Job Listing</h4></div>
			<div class = "modal-body">
				<div class = "row">
					<input class = "form-control" id  = "jobNameSearchInput" placeholder = "Search By Job Name" type = "text">
				</div>
				<table class = "table table-condensed table-striped" id = "jobListTable"></table>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-default" data-dismiss = "modal">Close</button>
 			</div>
		</div>
	</div>
</div>

<g:javascript>
	$( "#jobSearchDialog" ).on( "hidden.bs.modal", function ( event ) { hideDialog( "jobSearchDialog" ); });
	$( "#jobSearchDialog" ).on( "shown.bs.modal", function ( event ) { displayDialog( "jobSearchDialog" ); });
	$( "#jobNameSearchInput" ).on( "input", function () { jobSearch(); });
</g:javascript>

<div class = "modal" id = "sourceSelectionDialog" role = "dialog" tabindex = "-1">
	<div class = "modal-dialog modal-lg">
 		<div class = "modal-content">
			<div class = "modal-header"><h4>Source Selection Table</h4></div>
			<div class = "modal-body">
				<div class = "row">
					<div align = "center" class = "col-md-6">
						Overall CE: <br>
						<span id = "overallCeSpan">-</span>
					</div>
					<div align = "center" class = "col-md-6">
						Overall LE: <br>
						<span id = "overallLeSpan">-</span>
					</div>
				</div>
				<table class = "table table-condensed table-striped" id = "sourceSelectionTable"></table>
			</div>
			<div class = "modal-footer">
				<button type = "button" class = "btn btn-primary" id = "selectImagesButton">Select</button>
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
				<div class = "row">
					<div class = "col-md-5"><div id = "tiePointImageIdDiv">&nbsp;</div></div>
					<div class = "col-md-2"><div align = "center" id = "tiePointImageCountDiv">&nbsp;</div></div>
					<div class = "col-md-5"><div align = "right" id = "tiePointAcquisitionDateDiv"></div></div>
				</div>
                <div class = "map" id = "tiePointMap"></div>
			</div>
			<div class = "modal-footer">
				<button class = "btn btn-primary" onclick = "changeTiePointFrame( 'rewind' )" type = "button">
					<span class = "glyphicon glyphicon-step-backward"></span>
				</button>
				<button class = "btn btn-primary" onclick = "changeTiePointFrame( 'fastForward' )" type = "button">
					<span class = "glyphicon glyphicon-step-forward"></span>
				</button>
				<button type = "button" class = "btn btn-primary" onclick = addTiePoint()>Add Tie Point</button>
				<button type = "button" class = "btn btn-primary" onclick = "$( '#jobNameDialog' ).modal( 'show' )">Complete</button>
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
