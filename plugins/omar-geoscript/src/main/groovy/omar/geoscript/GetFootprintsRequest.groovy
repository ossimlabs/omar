package omar.geoscript

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 1/4/16.
 */
@ToString( includeNames = true )

class GetFootprintsRequest implements Validateable
{
  String bbox
  String srs

  Integer width
  Integer height

  String format
  Boolean transparent
  String layers
  String styles
  String filter
}
