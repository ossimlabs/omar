package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString
import groovy.xml.StreamingMarkupBuilder

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

  String toXML()
  {
    def prefix = typeName?.split( ':' )?.first()

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          xsi: "http://www.w3.org/2001/XMLSchema-instance",
          "${prefix}": namespace
      )
      DescribeFeatureType(
          service: 'WFS',
          version: version,
          request: 'DescribeFeatureType',
          xmlns: "http://www.opengis.net/wfs",
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/${version}/wfs.xsd"
      ) {
        TypeName( typeName )
      }
    }

    return new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x ).toString()
  }

  static DescribeFeatureTypeRequest fromXML(String xml)
  {
    def x = new XmlSlurper().parseText( xml )
    def typeName = x?.TypeName?.text()
    def prefix = typeName?.split( ':' )?.first()
    def specifiedVersion = WfsParseUtil.findVersion( x )

    return new DescribeFeatureTypeRequest(
        service: x?.@service?.text(),
        version: specifiedVersion,
        request: 'GetFeature',
        typeName: typeName,
        namespace: x?.lookupNamespace( prefix )
    )
  }
}
