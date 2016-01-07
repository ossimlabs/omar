package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 9/22/15.
 */

@ToString( includeNames = true )
class GetFeatureRequest implements Validateable
{
  static mapWith = 'none'

  String service
  String version
  String request

  String typeName
  String namespace
  String filter
  String resultType
  String outputFormat
  String sortBy
  String propertyName

  Integer maxFeatures
  Integer startIndex

  static mapping = {
    version false
  }

  String toXML()
  {
    return ""
  }

  static GetFeatureRequest fromXML(String xml)
  {
    def x = new XmlSlurper().parseText( xml )
    def typeName = x?.Query?.@typeName?.text()
    def prefix = typeName?.split( ':' )?.first()
    String specifiedVersion = WfsParseUtil.findVersion( x )
    def maxFeatures = x?.@maxFeatures?.text()
    def startIndex = x?.@startIndex?.text()

    return new GetFeatureRequest(
        service: 'WFS',
        version: specifiedVersion,
        request: 'GetFeature',
        typeName: typeName,
        namespace: x?.lookupNamespace( prefix ) ?: null,
        outputFormat: x?.@outputFormat?.text() ?: null,
        maxFeatures: ( maxFeatures ) ? maxFeatures?.toInteger() : null,
        startIndex: ( startIndex ) ? startIndex?.toInteger() : null,
        resultType: x?.@resultType?.text(),
        filter: WfsParseUtil.getFilterAsString( x ),
        sortBy: x?.Query?.first()?.SortBy?.text(),
        propertyName: x?.Query?.first()?.PropertyName?.collect { it?.text() }?.join( ',' )
    )

  }
}
