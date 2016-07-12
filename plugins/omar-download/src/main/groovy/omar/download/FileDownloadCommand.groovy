package omar.download

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by nroberts on 7/8/16.
 */
@ToString(includeNames = true)
class FileDownloadCommand implements Validateable
{
    String type
    def archiveOptions
    def fileGroups
    def zipFileName
}
