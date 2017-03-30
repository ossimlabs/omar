function buildJobsTable( jobs ) {
    var table = $( "#jobListTable" )[ 0 ];
    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    row.insertCell( row.cells.length );
    $.each(
        [ "Name", "Registration", "DEM", "TLV", "Submitted" ],
        function( index, value ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( "<b><i>" + value + "</i></b>" );
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

            var imageRegistration = job.imageRegistration
            cell = row.insertCell( row.cells.length );
            if ( imageRegistration.status == "FINISHED" ) {
                var button = document.createElement( "button" );
                button.className = "btn btn-primary btn-xs";
                button.innerHTML = "View ISA";
                button.onclick = function() {
                    /* need to launch in ISA */
                }
                $( cell ).append( button );
            }
            else { $( cell ).append( imageRegistration.status ); }

            var demGeneration = imageRegistration.demGeneration;
            cell = row.insertCell( row.cells.length );
            var demGenerationStatus = demGeneration.status || "-";
            var cellText;
            if ( demGenerationStatus == "FINISHED" ) {
                var button = document.createElement( "button" );
                button.className = "btn btn-primary btn-xs";
                button.innerHTML = "View 3D";
                button.onclick = function() {
                    window.open( tlv.contextPath + "?demGeneration=" + demGeneration.id + "&dimensions=3&tilt=60" );
                }
                $( cell ).append( button );
            }
            else { $( cell ).append( demGenerationStatus ); }

            cell = row.insertCell( row.cells.length );
            var button = document.createElement( "button" );
            button.className = "btn btn-primary btn-xs";
            button.innerHTML = "View 2D";
            button.onclick = function() {
                window.open( tlv.contextPath + "?3disa=" + job.id );
            }
            $( cell ).append( button );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( job.submitted.replace(/T/, " ") );

            var colSpan = row.cells.length;
            row = table.insertRow( table.rows.length );
            $( row ).css( "display", "none" );
            cell = row.insertCell( row.cells.length );
            $( cell ).attr( "colspan", colSpan );
            $( cell ).append(
                "<table class = 'table table-condensed'>" +
                    "<tr>" +
                        "<td></td>" +
                        "<td><b><i>Image Registration</i></b></td>" +
                        "<td><b><i>DEM Generation</i></b></td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td>Start</td>" +
                        "<td>" + ( imageRegistration.start ? imageRegistration.start.replace( /T/, " " ) : "-" ) + "</td>" +
                        "<td>" + ( demGeneration.start ? demGeneration.start.replace( /T/, " " ) : "-" ) + "</td>" +
                    "</tr>" +
                    "<tr>" +
                        "<td>Finish</td>" +
                        "<td>" + ( imageRegistration.finish ? imageRegistration.finish.replace( /T/, " " ) : "-" ) + "</td>" +
                        "<td>" + ( demGeneration.finish ? demGeneration.finish.replace( /T/, " " ) : "-" ) + "</td>" +
                    "</tr>" +
                "</table>" +
                "<div class = 'row'>" +
                    "<div align = 'center' class = 'col-md-6'>" +
                        "<b><i>Layers: </i></b> " + imageRegistration.images.length +
                    "</div>" +
                    "<div align = 'center' class = 'col-md-6'>" +
                        "<b><i>Tie Points: </i></b>" + imageRegistration.images[0].tiePoints.length +
                    "</div>" +
                "</div>"
            );

            var table2 = document.createElement( "table" );
            table2.className = "table table-condensed";
            var row2 = table2.insertRow( table2.rows.length );
            var cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).append( "Filename" );

            cell2 = row2.insertCell( row2.cells.length );
            $( cell2 ).append( "Sensor Model" );

            $.each(
                imageRegistration.images,
                function( index, image ) {
                    row2 = table2.insertRow( table2.rows.length );
                    cell2 = row2.insertCell( row2.cells.length );
                    $( cell2 ).append( image.filename );
                    cell2 = row2.insertCell( row2.cells.length );
                    $( cell2 ).append( image.sensorModel );
                }
            );
            $( cell ).append( table2 );

            expandCollapseButton.onclick = function() {
                var span = $( this ).children()[0];
                if ( $( span ).hasClass( "glyphicon-plus" ) ) {
                    $( table.rows[ 2 * ( index + 1 ) ] ).show();
                    $( span ).removeClass( "glyphicon-plus" );
                    $( span ).addClass( "glyphicon-minus" );
                }
                else {
                    $( table.rows[ 2 * ( index + 1 ) ] ).hide();
                    $( span ).removeClass( "glyphicon-minus" );
                    $( span ).addClass( "glyphicon-plus" );
                }
                displayDialog( "jobSearchDialog" );
            }
        }
    );
}

function jobSearch() {
    $.ajax({
        data: "job=" + $( "#jobNameSearchInput" ).val(),
        dataType: "json",
        success: function( data ) {
            $( "#jobSearchDialog" ).modal( "show" );
            buildJobsTable( data );
            displayDialog( "jobSearchDialog" );
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
            var jobLayer = {
                filename: layer.metadata.filename,
                sensorModel: layer.sensorModel
            };

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

    tlv[ "3disa" ].job.bbox = new ol.format.WKT().writeGeometry(
        new ol.geom.Polygon.fromExtent(
            ol.proj.transformExtent( tlv.map.getView().calculateExtent( tlv.map.getSize() ), "EPSG:3857", "EPSG:4326" )
        )
    );
    tlv[ "3disa" ].job.name = encodeURIComponent( $( "#jobNameInput" ).val() );
    tlv[ "3disa" ].job.layers = jobLayers;
console.dir(tlv[ "3disa" ].job);
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
