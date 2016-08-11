package omar.geoscript

import geoscript.geom.Bounds
import geoscript.filter.Color
import geoscript.filter.Filter
import geoscript.render.Map as GeoScriptMap
import geoscript.style.Composite
import geoscript.workspace.Workspace
import static geoscript.style.Symbolizers.*

class FootprintService
{
  static transactional = false
  def grailsApplication
  def geoscriptService

  def getFootprints(GetFootprintsRequest params)
  {
    def ostream = new ByteArrayOutputStream()
    def (prefix, layerName) = params.layers.split( ':' )
    def styles = grailsApplication.config.wms.styles

    def layerInfo = LayerInfo.where {
      name == layerName && workspaceInfo.namespaceInfo.prefix == prefix
    }.get()

    Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->

      def outlineLookupTable = styles[params.styles]

      def style = outlineLookupTable.collect { k, v ->
        ( stroke( color: new Color( v.color ) ) + fill( opacity: 0.0 ) ).where( v.filter )
      }

      def x = outlineLookupTable.keySet().collect { "'${it}'" }.join( ',' )

      style << ( stroke( color: '#000000' ) + fill( opacity: 0.0 ) ).where( "file_type not in (${x})" )

      def footprints = new QueryLayer( workspace[layerName], style as Composite )
      def viewBbox = new Bounds( *( params.bbox.split( ',' )*.toDouble() ), params.srs )
      def geomField = workspace[layerName].schema.geom
      def queryBbox

      if ( !workspace[layerName]?.proj?.equals( viewBbox?.proj ) )
      {
        queryBbox = viewBbox.reproject( workspace[layerName]?.proj )
      }
      else
      {
        queryBbox = viewBbox
      }

      def filter = Filter.intersects( geomField.name, queryBbox.geometry )

      if ( params.filter )
      {
        filter = filter.and( new Filter( params.filter ) )
      }

      footprints.filter = filter

      def map = new GeoScriptMap(
          width: params.width,
          height: params.height,
          type: params.format.split( '/' ).last(),
          bounds: viewBbox,
          proj: viewBbox.proj,
          layers: [footprints]
      )

      map.render( ostream )
      map.close()
    }

    [contentType: params.format, buffer: ostream.toByteArray()]
  }
}
