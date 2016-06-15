package omar.avro
import omar.core.BindUtil
import grails.converters.JSON
import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*

@Api(value = "Avro",
     description = "API operations for AVRO Payload manipulation",
     produces = 'application/json',
     consumes = 'application/json'
)
class AvroController {
   def avroService
   static allowedMethods = [index:["GET"],
                            addFile:["POST", "GET"],
                            listFile:["GET"],
                            resetFileProcessingStatus:["POST","GET"],
                            addMessage:["POST"],
                            listMessage:["GET"]
                            ]

   def index() { render "" }

   @ApiOperation(value = "Add a file", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 
            'filename', value = 'File to have posted and indexed', required=true, paramType = 'query', dataType = 'string'),
   ])
   def addFile()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new IndexFileCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( IndexFileCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.addFile(cmd)

      response.status = result.status
      render contentType: "application/json", text: result as JSON

   }
   @ApiOperation(value = "List files", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'offset', value = 'Process Id', required=false, paramType = 'query', dataType = 'integer'),
           @ApiImplicitParam(name = 'limit', value = 'Process status', defaultValue = '', paramType = 'query', dataType = 'integer'),
   ])
   def listFile()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new GetFileCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( GetFileCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.listFile(cmd)

      render contentType: "application/json", text: result as JSON
   }
   @ApiOperation(value = "Reset File Processing Status", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'processId', value = 'Process Id', required=false, paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'status', value = 'Process status', allowableValues="[READY,PAUSED,CANCELED,FINISHED,FAILED]",  defaultValue = 'READY', paramType = 'query', dataType = 'string'),
           @ApiImplicitParam(name = 'whereStatus', value = 'Where status equals', allowableValues="[READY,PAUSED,CANCELED,FINISHED,FAILED,RUNNING]",  defaultValue = '', paramType = 'query', dataType = 'string'),
   ])
   def resetFileProcessingStatus()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new ResetFileProcessingCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( ResetFileProcessingCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.resetFileProcessingCommand(cmd)

      response.status = result.status
      render contentType: "application/json", text: result as JSON

   }
   @ApiOperation(value = "Add message", consumes= 'application/json', produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'body',
                   value = "General Message for a single record Avro Payload",
                   paramType = 'body',
                   dataType = 'string')
   ])
   def addMessage()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new IndexMessageCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( IndexMessageCommand, requestParams )
      bindData( cmd, requestParams )
      if(!cmd.message&&jsonData)
      {
         cmd.message = request.JSON
      }
      HashMap result = avroService.addMessage(cmd)

      response.status = result.status
      render contentType: "application/json", text: result as JSON
   }

   @ApiOperation(value = "List Messages", consumes= 'application/json',
                 produces='application/json', httpMethod="POST")
   @ApiImplicitParams([
           @ApiImplicitParam(name = 'offset', value = 'Process Id', required=false, paramType = 'query', dataType = 'integer'),
           @ApiImplicitParam(name = 'limit', value = 'Process status', defaultValue = '', paramType = 'query', dataType = 'integer'),
   ])
   def listMessage()
   {
      def jsonData = request.JSON?request.JSON as HashMap:null
      def requestParams = params - params.subMap( ['controller', 'action'] )
      def cmd = new GetMessageCommand()

      // get map from JSON and merge into parameters
      if(jsonData) requestParams << jsonData
      BindUtil.fixParamNames( GetMessageCommand, requestParams )
      bindData( cmd, requestParams )
      HashMap result = avroService.listMessage(cmd)

      render contentType: "application/json", text: result as JSON
   }
}
