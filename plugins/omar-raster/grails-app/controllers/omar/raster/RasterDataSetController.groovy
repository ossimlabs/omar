package omar.raster

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import grails.converters.JSON
import omar.core.HttpStatusMessage
import omar.core.BindUtil
import groovy.json.JsonOutput

@Api( value = "dataManager",
		description = "DataManager Support"
)
class RasterDataSetController
{
	static allowedMethods = [
			addRaster: 'POST',
			removeRaster: 'POST' ,
			getRasterFilesProcessing: 'GET'
	]

	def rasterDataSetService
	@ApiOperation( value = "Add a Raster to the database",
			produces = 'text/plain',
			httpMethod = 'POST',
			notes = """
    The service api <b>addRaster</b>
    <br><br>
    <H2>Parameter List</H2>
    <br><br>
    <ul>
        <li>
            <b>filename</b><p/>
				Pass the filename to be added and indexed into the database
        </li>
        <br>
        <li>
            <b>background</b><p/>
            If the parameter is true it will indicate to submit the process as a background job.
        </li>
        <br>
        <li>
            <b>buildOverviews</b><p/>
            If the parameter is true it will indicate to build overviews for the image.  Also
            known as reduced resolution sets.
        </li>
        <br>
        <li>
            <b>buildHistograms</b><p/>
            If the parameter is true it will indicate to build histograms for the image.
        </li>
        <br>
        <li>
            <b>buildHistogramsWithR0</b><p/>
            If the parameter is true and buildHistograms is true then it will use the full resolution
            to build the histograms.  Ususally this is specified if you have images with internal overviews.
            If internal overviews are found and buildHistograms is true and  buildHistogramsWithR0 is false then it will
            build using the first iteration of the overview and that is whatever the last level the internal overviews
            end.  So if you have an image with internal 5 levels then it will use the 5th level for the histogram.  If
        </li>
        <br>
        <li>
            <b>useFastHistogramStaging</b><p/>
            If enabled it will only use a few tiles in the image to calculate the histogram
        </li>
        <br>
        <li>
            <b>overviewType</b><p/>
            Indicates the overview type to use.
        </li>
        <br>
        <li>
            <b>overviewCompressionType</b><p/>
            Indicates the type of compression to use during the building of the overviews.
        </li>
        <br><br>
        <b>Additional Notes</b><br><br>
        You can also pass the arguments as a JSON string and post to the URL.  The format supported:
        <pre>
        {
          "filename": "",
          "background: "",
          "buildOverviews": "",
          "buildHistograms": "",
          "overviewType": "",
          "overviewCompressionType":""
        }
        </pre>
    <ul>
    """)
	@ApiImplicitParams( [
			@ApiImplicitParam( name = 'filename', value = 'Path to file to add', dataType = 'string', required = true ),
			@ApiImplicitParam( name = 'background', value = 'Process in the background', allowableValues="[true,false]", defaultValue="true", dataType = "boolean",  required = false),
			@ApiImplicitParam( name = 'buildOverviews', value = 'Build overviews', allowableValues="[true,false]", defaultValue="true", dataType = "boolean", required = false),
			@ApiImplicitParam( name = 'buildHistograms', value = 'Build histograms', allowableValues="[true,false]", defaultValue="true", dataType = "boolean", required = false),
			@ApiImplicitParam( name = 'buildHistogramsWithR0', value = 'Build histograms with R0', allowableValues="[true,false]", defaultValue="false", dataType = "boolean", required = false),
			@ApiImplicitParam( name = 'useFastHistogramStaging', value = 'Fast Histogram calculation', allowableValues="[true,false]", defaultValue="false", dataType = "boolean", required = false),
			@ApiImplicitParam( name = 'overviewType', value = 'Overview type', allowableValues="[ossim_tiff_box, ossim_tiff_nearest, ossim_kakadu_nitf_j2k]", defaultValue = "ossim_tiff_box", dataType = "string", required = false),
			@ApiImplicitParam( name = 'overviewCompressionType', value = 'Overview compression type', allowableValues="[NONE,JPEG,PACKBITS,DEFLATE]", defaultValue="NONE", dataType = "string", required = false),
	] )
	def addRaster()
	{
		def jsonData = request.JSON?request.JSON as HashMap:null
		def requestParams = params - params.subMap( ['controller', 'action'] )
		def cmd = new AddRasterCommand()

		// get map from JSON and merge into parameters
		if(jsonData) requestParams << jsonData
		BindUtil.fixParamNames( AddRasterCommand, requestParams )
		bindData( cmd, requestParams )

		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.addRaster( httpStatusMessage, cmd )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	@ApiOperation( value = "Remove a Raster from the database", produces = 'text/plain', httpMethod = 'POST' )
	@ApiImplicitParams([
			@ApiImplicitParam( name = 'deleteFiles', value = 'Delete the image file and all support files linked to it in the database (e.g. his, ovr, etc.)', allowableValues="[true,false]", defaultValue="false", dataType = "boolean",  required = false),
			@ApiImplicitParam( name = 'filename', value = 'Path to file to remove', dataType = 'string', required = true ),
	])
	def removeRaster()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = rasterDataSetService.removeRaster( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	@ApiOperation( value = "Returns the processing status of the raster files",
			         produces = 'application/json',
			httpMethod = 'GET' )
	@ApiImplicitParams( [
			@ApiImplicitParam(name = 'offset', value = 'Process Id', required=false, paramType = 'query', dataType = 'integer'),
			@ApiImplicitParam(name = 'limit', value = 'Process status', defaultValue = '', paramType = 'query', dataType = 'integer'),
	 ] )
	def getRasterFilesProcessing()
	{
		def jsonData = request.JSON?request.JSON as HashMap:null
		def requestParams = params - params.subMap( ['controller', 'action'] )
		def cmd = new GetRasterFilesProcessingCommand()

		// get map from JSON and merge into parameters
		if(jsonData) requestParams << jsonData
		BindUtil.fixParamNames( GetRasterFilesProcessingCommand, requestParams )
		bindData( cmd, requestParams )
		HashMap result = rasterDataSetService.getFileProcessingStatus(cmd)

		render contentType: "application/json", text: result as JSON
	}


	@ApiOperation( value = "Returns the Files assoicated with a given raster ID",
			produces = 'application/json',
			httpMethod = 'GET',
			notes = """
    The service api <b>getRasterFiles</b>
    <br><br>
    <H2>Parameter List</H2>
    <br><br>
    <ul>
        <li>
            <b>id</b><p/>
				This can be the record ID, image ID, or the indexId for a entry to search for
        </li>
        <br>
    <ul>
""")
	@ApiImplicitParams( [
			@ApiImplicitParam(name = 'id', value = 'Search Id', required=false, paramType = 'query', dataType = 'string'),
	] )
	def getRasterFiles()
	{
		def jsonData = request.JSON?request.JSON as HashMap:null
		def requestParams = params - params.subMap( ['controller', 'action'] )
		def cmd = new GetRasterFilesCommand()

		// get map from JSON and merge into parameters
		if(jsonData) requestParams << jsonData
		BindUtil.fixParamNames( GetRasterFilesCommand, requestParams )
		bindData( cmd, requestParams )
		HashMap result = rasterDataSetService.getRasterFiles(cmd)

		render contentType: "application/json", text: result as JSON
	}

	@ApiOperation(
		value = "Returns an array of distinct values in the Raster Entry table for a given column name",
		produces = 'application/json',
		httpMethod = 'GET'
	)
	@ApiImplicitParams([
		@ApiImplicitParam(
			allowableValues = "[countryCode, missionId, sensorId, targetId]",
 			dataType = 'string',
			defaultValue = "countryCode",
			name = 'property',
			paramType = 'query',
			required = true,
			value = 'Column Name'
		)
	])
	def getDistinctValues() {
		def results = []
		switch (params.property) {
			case "countryCode" :
			case "missionId" :
			case "sensorId" :
			case "targetId" :
				results = RasterEntry.withCriteria {
					projections {
						distinct("${params.property}")
					}
				}
		}


		render contentType: "application/json", text: JsonOutput.toJson(results.findAll({ it != null }))
	}
}
