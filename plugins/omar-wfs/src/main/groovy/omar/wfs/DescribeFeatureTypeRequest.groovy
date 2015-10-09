package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 9/4/15.
 */
@ToString( includeNames = true )
class DescribeFeatureTypeRequest implements Validateable
{
  static mapWith = 'none'

  String service
  String version
  String request

  String typeName
  String namespace

  static mapping = {
    version false
  }

}
