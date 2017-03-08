function buildJobsTable( jobs ) {
    var table = $( "#jobListTable" )[ 0 ];
    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    row.insertCell( row.cells.length );
    $.each(
        [ "Name", "DEM", "Layers", "Registration", "Sensor Model", "Submitted", "Tie Points" ],
        function( index, value ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( value );
        }
    );

    $.each(
        jobs,
        function( index, job ) {
            row = table.insertRow( table.rows.length );

            cell = row.insertCell( row.cells.length );
            var expandCollapseButton = document.createElement( "button" );
            expandCollapseButton.className = "btn btn-primary btn-xs";
            expandCollapseButton.innerHTML = "<span class = 'glyphicon glyphicon-plus'></span>";
            $( cell ).append( expandCollapseButton );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( decodeURIComponent( job.name ) );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.demGeneration.status || "-" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.imageRegistration.tiePoints.unique( "filename" ).length );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.imageRegistration.status );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.sensorModel );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.submitted );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.imageRegistration.tiePoints.length );


            var table2 = document.createElement( "table" );
            table2.className = "table";
            var row2 = table2.insertRow( table2.rows.length );
            var cell2 = row2.insertCell( row2.cells.length );

            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( "Image Registration" );

            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( "DEM Generation" );

            row2 = table2.insertRow( table2.rows.length );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).append( "Start" );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( job.imageRegistration.start || "-" );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( job.demGeneration.start || "-" );

            row2 = table2.insertRow( table2.rows.length );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).append( "Finish" );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( job.imageRegistration.finish || "-" );
            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).attr( "align", "center" );
            $( cell2 ).append( job.demGeneration.finish || "-" );

            var colSpan = row.cells.length;
            row = table.insertRow( table.rows.length );
            $( row ).css( "display", "none" );
            cell = row.insertCell( row.cells.length );
            $( cell ).attr( "colspan", colSpan );
            $( cell ).append( table2 );

            expandCollapseButton.onclick = function() {
                var span = $( this ).children()[0];
                if ( $( span ).hasClass( "glyphicon-plus" ) ) {
                    $( table.rows[ 2 * ( index + 1 ) ] ).show();
                    displayDialog( "jobSearchDialog" );
                    $( span ).removeClass( "glyphicon-plus" );
                    $( span ).addClass( "glyphicon-minus" );
                }
                else {
                    $( table.rows[ 2 * ( index + 1 ) ] ).hide();
                    displayDialog( "jobSearchDialog" );
                    $( span ).removeClass( "glyphicon-minus" );
                    $( span ).addClass( "glyphicon-plus" );
                }
            }
        }
    );
}

function jobSearch() {
    $.ajax({
        data: "jobName=" + $( "#jobNameSearchInput" ).val(),
        dataType: "json",
        success: function( data ) {
            $( "#jobSearchDialog" ).modal( "show" );
            displayDialog( "jobSearchDialog" );
            buildJobsTable( data );
            hideLoadingDialog();
        },
        url: tlv.contextPath + "/threeDisa/listJobs"
    });

}

function submitJob() {
    var validationFlag = true;

    var jobLayers = [];
    $.each(
        tlv[ "3disa" ].layers,
        function( index, layer ) {
            var jobLayer = { filename: layer.metadata.filename };

            var features = layer.vectorLayer.getSource().getFeatures();
            features.sort( function( a, b ) {
                var aNumber = parseInt( a.getStyle().getText().getText(), 10 );
                var bNumber = parseInt( b.getStyle().getText().getText(), 10 );


                return  aNumber > bNumber ? 1 : ( bNumber > aNumber ? -1 : 0 );
            });

            jobLayer.tiePoints = [];
            $.each(
                features,
                function( index, feature ) {
                    var coordinates = feature.getGeometry().getCoordinates();
                    jobLayer.tiePoints.push( coordinates );
                }
            );
            if ( jobLayer.tiePoints.length < 1 ) {
                displayErrorDialog( "We're going to need some tie points... good ones please!" );
                validationFlag = false;
                return;
            }

            jobLayers.push( jobLayer );
        }
    );

    tlv[ "3disa" ].job.name = encodeURIComponent( $( "#jobNameInput" ).val() );
    tlv[ "3disa" ].job.layers = jobLayers;
    tlv[ "3disa" ].job.sensorModel = $( "#sensorModelSelect" ).val();

    if ( validationFlag ) {
        $( "#tiePointSelectionDialog" ).modal( "hide" );
        displayLoadingDialog( "Submitting your job details, hang tight." );
        $.ajax({
            data: "jobParams=" + encodeURIComponent( JSON.stringify( tlv[ "3disa" ].job ) ),
            dataType: "json",
            success: function( data ) {
                hideLoadingDialog();
                if ( !data.response ) { displayErrorDialog( "Sorry, your job did not save properly." ); }
                else { jobSearch(); }
            },
            url: tlv.contextPath + "/threeDisa/submit3DisaJob"
        });
    }
}
