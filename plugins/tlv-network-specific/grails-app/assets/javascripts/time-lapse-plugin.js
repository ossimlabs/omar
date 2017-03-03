function buildSummaryTable() {
    var table = $( "#timeLapseSummaryTable" )[ 0 ];

    for ( var i = table.rows.length - 1; i >= 0; i-- ) { table.deleteRow( i ); }

    var row = table.insertRow( 0 );
    $( row ).css( "white-space", "nowrap" );
    var cell = row.insertCell( row.cells.length );
    row.insertCell( row.cells.length );
    $.each(
        [ "Image ID", "Acquisition Date", "NIIRS" ],
        function( index, value ) {
            var cell = row.insertCell( row.cells.length );
            $( cell ).append( value );
        }
    );

    $.each(
        tlv.layers,
        function( i, x ) {
            row = table.insertRow( table.rows.length );
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
