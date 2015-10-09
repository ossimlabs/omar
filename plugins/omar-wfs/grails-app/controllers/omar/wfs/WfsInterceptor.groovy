package omar.wfs

import omar.core.BindUtil

class WfsInterceptor
{

  public WfsInterceptor()
  {
    match( controller: 'wfs' )
    //println 'WfsInterceptor'
  }

  boolean before()
  {
//    println actionName

    switch ( actionName?.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      BindUtil.fixParamNames( GetFeatureRequest, params )
      break
    case 'DESCRIBEFEATURETYPE':
      BindUtil.fixParamNames( DescribeFeatureTypeRequest, params )
      break
    case 'GETFEATURE':
      BindUtil.fixParamNames( GetFeatureRequest, params )
      break
    }

    println params

    true
  }

  boolean after() { true }

  void afterView()
  {
    // no-op
  }
}
