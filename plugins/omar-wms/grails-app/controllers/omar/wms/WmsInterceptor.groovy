package omar.wms

import omar.core.BindUtil

class WmsInterceptor
{

  public WmsInterceptor()
  {
    match( controller: 'wms' )
    //println 'WfsInterceptor'
  }

  boolean before()
  {
    switch ( actionName?.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      BindUtil.fixParamNames( GetCapabilitiesRequest, params )
      break
    case 'GETMAP':
      BindUtil.fixParamNames( GetMapRequest, params )
      OldMARCompatibility.translate(params)
      break
    }

    true
  }

  boolean after() { true }

  void afterView()
  {
    // no-op
  }
}
