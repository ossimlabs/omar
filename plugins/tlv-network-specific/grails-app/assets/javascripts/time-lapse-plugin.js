function buildSummaryTable() {
    var table = $( "#timeLapseSummaryTable" )[ 0 ];

    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    var cell = row.insertCell( row.cells.length );
    row.insertCell( row.cells.length );
    var keys = [ "imageId", "acquisitionDate", "niirs" ];
    $.each(
        keys,
        function( i, x ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( x.capitalize().replace( /([A-Z])/g, " $1" ) );
        }
    );

    $.each(
        tlv.layers,
        function( i, x ) {
            row = table.insertRow( table.rows.length );
            $( row ).click( function() {
                if ( $( this ).hasClass( "success" ) ) {
                    $( this ).removeClass( "success" );
                }
                else { $( this ).addClass( "success" ); }
            } );
            $( row ).css( "cursor", "pointer" );
            $( row ).mouseout( function() { $( this ).removeClass( "info" ); } );
            $( row ).mouseover( function() { $(  this ).addClass( "info" ); } );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( i + 1 );

            cell = row.insertCell( row.cells.length );
            $( cell ).css( "white-space", "nowrap" );
            var downButton = "<span class = 'glyphicon glyphicon-arrow-down' title = 'Move Down'></span>";
            var upButton = "<span class = 'glyphicon glyphicon-arrow-up' title = 'Move Up'></span>";

            if ( i == 0 ) {
                $( cell ).append( "<a href = javascript:moveLayerDownInStack(" + i + ");buildSummaryTable();>" + downButton + "</a>" );
            }
            else if ( i == tlv.layers.length - 1 ) { $( cell ).append( "<a href = javascript:moveLayerUpInStack(" + i + ");buildSummaryTable();>" + upButton + "</a>" ); }
            else {
                $( cell ).append( "<a href = javascript:moveLayerUpInStack(" + i + ");buildSummaryTable();>" + upButton + "</a>" );
                $( cell ).append( "<a href = javascript:moveLayerDownInStack(" + i + ");buildSummaryTable();>" + downButton + "</a>" );
            }

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.imageId );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.acquisitionDate + "z" );

            cell = row.insertCell( row.cells.length );
            $( cell ).append( x.metadata.niirs );

            cell = row.insertCell( row.cells.length );
            var span = document.createElement( "span" );
            span.className = "glyphicon glyphicon-trash";

            var deleteButton = document.createElement( "button" );
            deleteButton.className = "btn btn-primary btn-xs";
            deleteButton.onclick = function() {
                deleteFrame( i );
                buildSummaryTable();
            };
            deleteButton.appendChild( span );
            $( cell ).append( deleteButton );
        }
    );
}

function getSelectedImages() {
    var selectedImages = [];
    var table = $( "#timeLapseSummaryTable" )[ 0 ];
    $.each(
        tlv.layers,
        function( i, x ) {
            var row = table.rows[ i ];
            if ( $( row ).hasClass( "success" ) ) {
                selectedImages.push( i );
            }
        }
    );


    return selectedImages;
}
