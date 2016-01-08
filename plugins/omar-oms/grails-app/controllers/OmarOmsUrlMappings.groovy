class OmarOmsUrlMappings
{

  static mappings = {

    "/imageSpace/getTile"( controller: 'imageSpace', action: 'index' )
    "/imageSpace/getTile"( controller: 'imageSpace', action: 'getTile' )
    "/imageSpace/getTileOverlay"( controller: 'imageSpace', action: 'getTileOverlay' )
    "/imageSpace/getThumbnail"( controller: 'imageSpace', action: 'getThumbnail' )

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
