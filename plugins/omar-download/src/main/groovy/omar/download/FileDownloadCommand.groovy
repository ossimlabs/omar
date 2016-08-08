package omar.download

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by nroberts on 7/8/16.
 */
@ToString(includeNames = true)
class FileDownloadCommand implements Validateable
{
    String type = "Download"
    def archiveOptions
    def fileGroups
    def zipFileName

    static constraints = {

        type(nullable:true)
        archiveOptions()
        fileGroups()
        zipFileName(nullable:true)
    }

}
