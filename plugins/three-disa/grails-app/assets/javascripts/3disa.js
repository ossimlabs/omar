function generateDem() {
    buildSummaryTable();
    $( "#summaryTableDialog" ).modal( "show" );

    $( "#selectImagesButton" ).unbind();
    $( "#selectImagesButton" ).show();
    $( "#selectImagesButton" ).click(
        function() {
            alert("This is where you would start making a DEM.");
        }
    );
}
