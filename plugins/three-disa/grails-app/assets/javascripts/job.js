function jobSearch() {
    $( "#jobSearchDialog" ).modal( "show" );

    $.ajax({
        data: "jobName=" + $( "#jobNameSearchInput" ).val(),
        dataType: "json",
        success: function( data ) {
            var table = $( "#jobListTable" )[ 0 ];

            for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

            var row = table.insertRow( 0 );
            $( row ).css( "white-space", "nowrap" );
            $.each(
                [ "Name", "Layers", "Registration", "Sensor Model", "Submitted", "Tie Points" ],
                function( index, value ) {
                    var cell = row.insertCell( row.cells.length );
                    $( cell ).append( value );
                }
            );

            $.each(
                data,
                function( index, job ) {
                    row = table.insertRow( table.rows.length );

                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( decodeURIComponent( job.name ) );

                    var layers = job.imageRegistration.tiePoints.unique( "filename" ).length;
                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( layers );

                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( job.imageRegistration.status );

                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( job.sensorModel );

                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( job.submitted );

                    var tiePoints = job.imageRegistration.tiePoints.length / layers;
                    cell = row.insertCell( row.cells.length );
                    $( cell ).append( tiePoints );
                }
            );
        },
        url: tlv.contextPath + "/threeDisa/listJobs"
    });

}

function submitJob() {
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
                return;
            }

            jobLayers.push( jobLayer );
        }
    );

    tlv[ "3disa" ].job.name = encodeURIComponent( $( "#jobNameInput" ).val() );
    tlv[ "3disa" ].job.layers = jobLayers;
    tlv[ "3disa" ].job.sensorModel = $( "#sensorModelSelect" ).val();

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
