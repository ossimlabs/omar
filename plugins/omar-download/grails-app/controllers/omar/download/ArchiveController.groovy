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
            notes="""
            <ul>
                <li>
                    Currently, only the download type and the zip archive options type are supported.
                </li>
                </br>
                <li>
                    The zip file name is optional and will use a preset file name if one is not entered.</br>
                    When entering a zip file name, be sure to enter a ".zip" extension (ex. myimages.zip).
                </li>
                </br>
                <li>
                    Enter a file groups root directory if you wish to keep the directory structure when you
                    unzip your zip file.</br>
                    File group files is a list of paths to files or folders that contain the inage information.</br>
                    here can be single or multiple file groups.

                    </br>
                    <pre>

                    <strong>Example of a accepted single and multiple file groups</strong>

                        "fileGroups":
                            [
                                {
                                    "rootDirectory":"",
                                    "files":["","",...]
                                }
                            ]

                            OR

                        "fileGroups":
                            [
                                {
                                    "rootDirectory":"",
                                    "files":["","",...]
                                }
                                {
                                    "rootDirectory":"",
                                    "files":["","",...]
                                }
                            ]
                    </pre>
                </li>
            </ul>
        """)

    @ApiImplicitParams([
        @ApiImplicitParam(
                name = 'body',
                value = "General Message for querying recommendations",
                defaultValue = """
                {
                    "type":"Download",
                    "zipFileName": "",
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
