package omar.wfs

import grails.validation.Validateable
import groovy.transform.ToString
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

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

  String toXML()
  {
    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          xsi: "http://www.w3.org/2001/XMLSchema-instance",
          wfs: "http://www.opengis.net/wfs"
      )
      wfs.GetCapabilities(
          service: 'WFS',
          vesion: version,
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/${version}/wfs.xsd"
      )
    }
    return new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x ).toString()
  }

  static GetCapabilitiesRequest fromXML(String xml)
  {
    def x = new XmlSlurper().parseText( xml )
    String specifiedVersion = WfsParseUtil.findVersion( x )

    return new GetCapabilitiesRequest(
        service: 'WFS',
        version: specifiedVersion,
        request: 'GetCapabilities'
    )
  }
}
