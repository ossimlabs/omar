//= require job
//= require source-selection
//= require tie-points


function cleanup3Disa() {
    $.each( tlv[ "3disa" ].layers, function( index, layer ) { layer.map.setTarget( null ); } );
    tlv[ "3disa" ] = {};
    $( "#tiePointMaps" ).html( "" );
}

function generateDem() {
    cleanup3Disa();
    tlv[ "3disa" ].job = { demGeneration: true };

    buildSourceSelectionTable();
    $( "#sourceSelectionDialog" ).modal( "show" );

    $( "#selectImagesButton" ).unbind();
    $( "#selectImagesButton" ).click( function() { setupTiePointSelectionDialog(); } );
}

var pageLoad3Disa = pageLoad;
pageLoad = function() {
	pageLoad3Disa();

	tlv[ "3disa" ] = {};
}
