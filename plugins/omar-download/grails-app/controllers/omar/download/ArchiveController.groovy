package omar.download

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiImplicitParam
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiOperation
import groovy.json.JsonSlurper
import omar.core.BindUtil

import javax.xml.ws.Response
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Api(
        value = "download",
        description = "API operations for Download IO",
        produces = 'application/json',
        consumes = 'application/json'
)

class ArchiveController {

    static allowedMethods = [
                              download:["POST"]
                            ]
    def archiveService

    @ApiOperation(
            value = "Download files",
            consumes= 'application/json',
            produces='application/json',
            httpMethod="POST",
            notes="""<insert notes here>"""
    )

    @ApiImplicitParams([
        @ApiImplicitParam(
                name = 'body',
                value = "General Message for querying recommendations",
                defaultValue = """
                {
                    "type":"Download",
                    "archiveOptions":
                    {
                        "type": "zip"
                    },
                    "fileGroups":
                    [
                        {
                            "rootDirectory":"",
                            "files":["",""]
                        }
                    ]
                }""",
                paramType = 'body',
                dataType = 'string'
            )
    ])

    def download() {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def fileInfoParams = params.fileInfo?params.fileInfo:null
        def requestParams = params - params.subMap(['controller', 'format', 'action'])
        def cmd = new FileDownloadCommand()

        if (fileInfoParams)
        {
            def slurper = new groovy.json.JsonSlurper()
            jsonData = slurper.parseText("${fileInfoParams}")
        }

        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( FileDownloadCommand, requestParams )
        bindData( cmd, requestParams )

        archiveService.download(response, cmd)

        null
    }
}
