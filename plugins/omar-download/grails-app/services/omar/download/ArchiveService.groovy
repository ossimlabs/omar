package omar.download

import grails.transaction.Transactional
import omar.core.HttpStatus
import grails.converters.JSON

import java.util.ArrayList


@Transactional
class ArchiveService {

    def download(def response, FileDownloadCommand cmd)
    {
        HashMap result = [
                status:HttpStatus.OK,
                message:"Downloading Files"
        ]

        String fileName = cmd.zipFileName

        if ((!fileName) || (fileName == ""))
        {
            fileName = "omar_images.zip"
        }

        if (cmd.validate())
        {
            try
            {
                if ((cmd.type?.toLowerCase() == "download") || (cmd.type == null))
                {
                    if((cmd.archiveOptions["type"].toString().toLowerCase() == "zip") || (cmd.archiveOptions["type"] == null))
                    {
                        if(cmd.fileGroups.size()>=1)
                        {
                            response.setContentType("application/octet-stream")
                            response.setHeader("Content-Disposition", "attachment;filename=${fileName}");
                            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
                            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                            if(cmd.fileGroups.size()==1)
                            {
                                HashMap listOfFilesAsMaps = cmd.fileGroups
                                ZipFiles zipFiles = new ZipFiles()
                                zipFiles.zipSingle(listOfFilesAsMaps, response.outputStream)
                            }
                            else
                            {
                                ArrayList listOfFilesAsMaps = cmd.fileGroups
                                ZipFiles zipFiles = new ZipFiles()
                                zipFiles.zipMulti(listOfFilesAsMaps, response.outputStream)
                            }
                        }
                        else
                        {
                            result.status =  omar.core.HttpStatus.NOT_ACCEPTABLE
                            result.message = "No File Group Specified"
                        }
                    }
                    else
                    {
                        result.status =  omar.core.HttpStatus.UNSUPPORTED_MEDIA_TYPE
                        result.message = "Archive Option Type Not Recognized"
                    }
                }
                else
                {
                    result.status =  omar.core.HttpStatus.NOT_ACCEPTABLE
                    result.message = "Request Type Not Recognized"
                }
            }
            catch (e)
            {
                result.status = omar.core.HttpStatus.BAD_REQUEST
                result.message = e.message
            }
        }
        else {
            def messages = []
            String message = "Invalid parameters"
            result = [status : omar.core.HttpStatus.BAD_REQUEST,
                      message: message.toString()]
        }

        if(result.status != HttpStatus.OK)
        {
            response.setContentType("application/json")
            response.status = result.status
            String jsonData = "${result as JSON}"

            response.outputStream.write(jsonData.bytes)
        }

        response.outputStream.close()

        result
    }
}
