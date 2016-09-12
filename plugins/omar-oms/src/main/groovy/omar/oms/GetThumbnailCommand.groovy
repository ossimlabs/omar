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
  Integer entry=0
  Integer size=128
  String format="jpeg"
}
