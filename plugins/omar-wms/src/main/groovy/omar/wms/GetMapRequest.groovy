package omar.wms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 12/2/15.
 */
@ToString( includeNames = true )
class GetMapRequest implements Validateable
{
  static mapWith = 'none'

  String service
  String version
  String request

  Integer width
  Integer height

  String srs
  String bbox
  String format
  String layers
  String styles

  Boolean transparent

  static mapping = {
    version false
  }

}
