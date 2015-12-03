package omar.wms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 12/2/15.
 */
@ToString( includeNames = true )
class GetCapabilitiesRequest implements Validateable
{
  static mapWith = 'none'

  String service
  String version
  String request

  static mapping = {
    version false
  }

}
