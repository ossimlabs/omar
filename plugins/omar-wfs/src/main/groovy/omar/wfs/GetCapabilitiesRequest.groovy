package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 9/3/15.
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
