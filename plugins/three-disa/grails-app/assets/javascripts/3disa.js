function cleanup3Disa() {
    var map = tlv[ "3disa" ].map;
    if ( map ) { map.setTarget( null ); }
    tlv[ "3disa" ] = {};
}

var disableMenuButtons3Disa = disableMenuButtons;
disableMenuButtons = function() {
    disableMenuButtons3Disa();

    var menuButtons = $(".navbar-nav")[0].children;
    $.each(
        menuButtons,
        function( index, button ) {
            if ( $( button ).text().contains( "3DISA" ) ) { $( button ).show() }
        }
    );
}

function generateDem() {
    cleanup3Disa();
    tlv[ "3disa" ].job = { demGeneration: true };

    buildSourceSelectionTable();
    $( "#sourceSelectionDialog" ).modal( "show" );

    $( "#selectImagesButton" ).unbind();
    $( "#selectImagesButton" ).click( function() { setupTiePointSelectionDialog(); } );
}

function getJobDetails() {
    displayLoadingDialog( "Getting job details..." );
    $.ajax({
        data: "job=" + tlv[ "3disa" ],
        dataType: "json",
        success: function( data ) {
            hideLoadingDialog();

            var job = data[0];
            var bbox = job.bbox;
            tlv.bbox = new ol.format.WKT().readGeometry( bbox ).getExtent();

            tlv.location = [
                ( tlv.bbox[0] + tlv.bbox[2] ) / 2,
                ( tlv.bbox[1] + tlv.bbox[3] ) / 2
            ];
            tlv.layers = [];

            var filenames = job.imageRegistration.images.map( function( image ) { return image.filename; } ); 
            getImageMetadata( filenames );
        },
        url: tlv.contextPath + "/threeDisa/listJobs"
    });
}

function getImageMetadata( filenames ) {
    displayLoadingDialog( "Getting metadata for the stack..." );
    $.each(
        filenames,
        function( index, filename ) {
            var searchParams = {
                filter: "filename LIKE '" + filename + "'",
                location: tlv.location,
                maxResults: 1
            };
            $.ajax({
                data: "searchParams=" + encodeURIComponent( JSON.stringify( searchParams ) ),
                dataType: "json",
                success: function(data) {
                    tlv.layers.push( data.layers[0] );

                    if ( tlv.layers.length == filenames.length ) {
                        tlv.layers.sort( function( a, b ) {
                            var aDate = a.acquisitionDate;
                            var bDate = b.acquisitionDate;


                            return  aDate > bDate ? 1 : ( bDate > aDate ? -1 : 0 );
                        });
                        hideLoadingDialog();
                        setupTimeLapse();
                    }
                },
                url: tlv.contextPath + "/search/searchLibrary"
            });
        }
    );
}

var pageLoad3Disa = pageLoad;
pageLoad = function() {
	pageLoad3Disa();

    if ( tlv[ "3disa" ] ) {
        $("#searchDialog").modal("hide");
        getJobDetails();
    }
    else if ( tlv.demGeneration ) {
        $("#searchDialog").modal("hide");

        tlv[ "3disa" ] = tlv.demGeneration;
        getJobDetails();
    }
	tlv[ "3disa" ] = {};
}
