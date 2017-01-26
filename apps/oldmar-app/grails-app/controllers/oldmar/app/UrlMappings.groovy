package oldmar.app

class UrlMappings
{
  static mappings = {
    "/wms/footprints"( controller: 'footprintsProxy' )
    "/ogc/wms"( controller: 'wmsProxy' )
    "/wfs"( controller: 'wfsProxy' )
    
    "/$controller/$action?/$id?(.$format)?" {
      constraints {
        // apply constraints here
      }
    }

    "/"( view: "/index" )
    "500"( view: '/error' )
    "404"( view: '/notFound' )
  }
}
