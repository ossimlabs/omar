package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString
import groovy.util.slurpersupport.GPathResult

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

  static GetFeatureRequest fromXML(String text)
  {
    def xml = new XmlSlurper().parseText( text )

    fromXML( xml )
  }

  static GetFeatureRequest fromXML(def xml)
  {
    def typeName = xml?.Query?.@typeName?.text()
    def namespacePrefix = typeName?.split( ':' )?.first()
    def namespaceUri = xml?.lookupNamespace( namespacePrefix ) ?: null
    String specifiedVersion = WfsParseUtil.findVersion( xml )
    def maxFeatures = xml?.@maxFeatures?.text()
    def startIndex = xml?.@startIndex?.text()
//    def namespace = "xmlns(${namespacePrefix}=${namespaceUri})"
    def propertyNames = xml?.Query?.first()?.PropertyName?.collect { it?.text()?.split( ':' )?.last() }?.join( ',' )


//    println namespace

    return new GetFeatureRequest(
        service: 'WFS',
        version: specifiedVersion,
        request: 'GetFeature',
        typeName: typeName,
//        namespace: namespace,
        outputFormat: xml?.@outputFormat?.text() ?: null,
        maxFeatures: ( maxFeatures ) ? maxFeatures?.toInteger() : null,
        startIndex: ( startIndex ) ? startIndex?.toInteger() : null,
        resultType: xml?.@resultType?.text(),
        filter: WfsParseUtil.getFilterAsString( xml ),
        sortBy: xml?.Query?.first()?.SortBy?.text(),
        propertyName: propertyNames
    )

  }
}
