class OmarOmsUrlMappings
{

  static mappings = {

    "/imageSpace/getTile"( controller: 'imageSpace', action: 'getTile' )
    "/imageSpace/getTileOverlay"( controller: 'imageSpace', action: 'getTileOverlay' )

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
