package omar.oms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 12/17/15.
 */
@ToString( includeNames = true )
class GetThumbnailCommand implements Validateable
{
  String filename
  Integer entry
  Integer size
  String format
}
