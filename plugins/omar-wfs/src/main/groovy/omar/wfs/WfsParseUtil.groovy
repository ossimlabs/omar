package omar.wfs

import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

/**
 * Created by sbortman on 10/15/15.
 */
class WfsParseUtil
{
  static String findVersion(GPathResult x)
  {
    def specifiedVersion = x?.@version?.text()
    def schemaLocation = x?.@'xsi:schemaLocation'?.text()

    if ( !specifiedVersion )
    {

      switch ( schemaLocation )
      {
      case ~".*/1.1.0/wfs.xsd":
        specifiedVersion = '1.1.0'
        break
      case ~".*/1.0.0/wfs.xsd":
      case ~".*/WFS-basic.xsd":
        specifiedVersion = '1.0.0'
        break
      }
    }
    specifiedVersion
  }

  static String getFilterAsString(GPathResult x)
  {
    (x?.Query?.Filter) ? x.Query.collect { new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim() }?.first() : null
  }
}
