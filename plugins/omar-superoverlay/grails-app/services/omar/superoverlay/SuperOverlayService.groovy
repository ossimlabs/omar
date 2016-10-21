package omar.superoverlay


import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel
import geoscript.geom.*
import geoscript.workspace.Workspace
import grails.transaction.Transactional
import groovy.xml.StreamingMarkupBuilder
import java.awt.image.BufferedImage
import joms.oms.ossimGpt
//import org.ossim.omar.core.Utility
//import org.ossim.omar.ogc.WmsCommand
import org.springframework.beans.factory.InitializingBean


@Transactional( readOnly = true )
class SuperOverlayService implements InitializingBean
{
  static transactional = false

  def grailsLinkGenerator
  def metersPerDegree
  def grailsApplication
  def rasterKmlService
  def webMappingService
  def tileSize = [width: 256, height: 256]
  def lodValues = [min: 128, max: 2000]
    def geoscriptService
//  def appTagLib = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

  def geometryFactory = new GeometryFactory( new PrecisionModel( PrecisionModel.FLOATING ), 4326 )

  def canSplit(def tileBounds, def fullResMetersPerPixel)
  {
    def metersPerPixel = getMetersPerPixel( tileBounds, fullResMetersPerPixel )

    // keep splitting if we can zoom further
    metersPerPixel > fullResMetersPerPixel
  }

  def createFullResBounds(def rasterEntry)
  {
    def bounds = rasterEntry.ground_geom.bounds
    def fullResBound = [minx: bounds.minX, miny: bounds.minY, maxx: bounds.maxX, maxy: bounds.maxY]
    def fullResMpp = rasterEntry.gsdy
    def deltax = fullResBound.maxx - fullResBound.minx
    def deltay = fullResBound.maxy - fullResBound.miny
    def degreesPerMeter = 1.0 / metersPerDegree
    def degreesPerPixel = degreesPerMeter * fullResMpp

    def pixelsWide = deltax / degreesPerPixel
    def pixelsHigh = deltay / degreesPerPixel
    def adjustedPixelsWide = Math.ceil( pixelsWide / tileSize.width ) * tileSize.width;
    def adjustedPixelsHigh = Math.ceil( pixelsHigh / tileSize.height ) * tileSize.height;

    fullResBound.maxx = fullResBound.minx + ( degreesPerPixel * adjustedPixelsWide )
    fullResBound.maxy = fullResBound.miny + ( degreesPerPixel * adjustedPixelsHigh )

    fullResBound
  }

  def createPolygonFromTileBounds(def bounds)
  {
    def coords = [
        new Coordinate( bounds.minx, bounds.miny ),
        new Coordinate( bounds.minx, bounds.maxy ),
        new Coordinate( bounds.maxx, bounds.maxy ),
        new Coordinate( bounds.maxx, bounds.miny ),
        new Coordinate( bounds.minx, bounds.miny )
    ] as Coordinate[]

    geometryFactory.createPolygon( geometryFactory.createLinearRing( coords ), null )
  }

  def createRootKml(def rasterEntry, def params)
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def newParams = new HashMap( params )
    def tileBounds = tileBound( params, fullResBound )
    def rasterEntryDescription = "" // rasterKmlService.createImageKmlDescription( rasterEntry )
    newParams.level = 0
    newParams.row = 0
    newParams.col = 0

    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
    //          name( "${rasterKmlService.createName( rasterEntry )}" )
          name( "${rasterEntry.filename}" )

          Snippet()
          description { mkp.yieldUnescaped( "<![CDATA[${rasterEntryDescription}]]>" ) }
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          NetworkLink() {
            open( "1" )
            Region() {
              Lod() {
                minLodPixels( lodValues.min )
                maxLodPixels( "-1" )
              }
              LatLonAltBox() {
                north( tileBounds.maxy )
                south( tileBounds.miny )
                east( tileBounds.maxx )
                west( tileBounds.minx )
              }
            }
            Link() {
              newParams.remove( "action" )
              newParams.remove( "controller" )

              href {
                mkp.yieldUnescaped(
                    """<![CDATA[${
                      grailsLinkGenerator.link(
                          absolute: true,
                          action: params.action, params: newParams )
                    }]]>""" )
              }
              viewRefreshMode( "onExpire" )
            }
          }
        }
      }
    }

    kmlbuilder.bind( kmlnode ).toString()
  }

  def createTileKml(def rasterEntry, def params)
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def tileBounds = tileBound( params, fullResBound )
    def wmsRequest = new WmsCommand()
    Utility.simpleCaseInsensitiveBind( wmsRequest, params )

    def newParams = new HashMap( params )
    newParams.remove( "action" )
    newParams.remove( "controller" )

    def edgeTileFlag = isAnEdgeTile( rasterEntry, fullResBound, params.level as Integer, params.row as Integer, params.col as Integer )
    def format = "image/jpeg"
    def transparent = false
    def ext = "jpg"
    def level = params.level as Integer

    if ( edgeTileFlag )
    {
      format = "image/png"
      transparent = true
      ext = "png"
    }
    Utility.simpleCaseInsensitiveBind( wmsRequest, [request: 'GetMap',
        layers: 'omar:raster_entry',
        filter: "in (${params.id})",
        srs: 'EPSG:4326',
        format: format,
        service: 'wms',
        version: '1.1.1',
        width: tileSize.width,
        height: tileSize.height,
        transparent: transparent,
        bbox: "${tileBounds.minx},${tileBounds.miny},${tileBounds.maxx},${tileBounds.maxy}"] )
    def wmsMap = wmsRequest.toMap()

    Utility.removeEmptyParams( wmsMap )
    String defaultWmsUrl = grailsLinkGenerator.link(
        absolute: true, controller: 'wms',
        action: 'getMap', params: wmsMap )

    if ( OmarSuperOverlayUtils.superOverlayConfig.wmsUrl )
    {
      URL wmsUrl = new URL( OmarSuperOverlayUtils.superOverlayConfig.wmsUrl )
      HashMap urlWmsParams = wmsMap + wmsUrl.params
      wmsUrl.setParams( urlWmsParams )

      defaultWmsUrl = wmsUrl.toString()
    }
    //println wmsMap

    //    println defaultWmsUrl

    //def minLod = Math.sqrt(tileSize.width*tileSize.height)
    //def maxLod = minLod

    def subtiles = []
    if ( canSplit( tileBounds, rasterEntry.metersPerPixel ) )
    {
      subtiles = generateSubTiles( params, fullResBound )
    }
    def defaultName = "${params.level}/${params.col}/${params.row}.kml"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
          name( defaultName )
          description()
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            Lod() {
              minLodPixels( lodValues.min )
              if ( subtiles.size() > 0 )
              {
                maxLodPixels( lodValues.max )
              }
              else
              {
                maxLodPixels( -1 )
              }
            }
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          GroundOverlay() {
            drawOrder( params.level )
            Icon() {
              href {
                mkp.yieldUnescaped( """<![CDATA[${
                  defaultWmsUrl
                }]]>""" )
    //                mkp.yieldUnescaped( """<![CDATA[${
    //                  grailsLinkGenerator.link(
    //                      absolute: true, controller: 'wms',
    //                      action: 'getMap', params: wmsMap )
    //                }]]>""" )
              }
              viewRefreshMode( "onExpire" )
            }
            LatLonBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          subtiles.each { tile ->
            newParams.level = tile.level
            newParams.row = tile.row
            newParams.col = tile.col
            NetworkLink {
              name( "${tile.level}/${tile.col}/${tile.row}.${ext}" )
              Region {
                Lod {
                  minLodPixels( lodValues.min )
                  maxLodPixels( "-1" )
                }
                LatLonAltBox {
                  north( "${tile.maxy}" )
                  south( "${tile.miny}" )
                  east( "${tile.maxx}" )
                  west( "${tile.minx}" )
                }
              }
              Link {
                href {
                  mkp.yieldUnescaped( """<![CDATA[${
                    grailsLinkGenerator.link( absolute: true,
                        action: params.action, params: newParams )
                  }]]>""" )
                }
                viewRefreshMode( "onExpire" )
              }
            }
          }
        }
      }
    }

    kmlbuilder.bind( kmlnode ).toString()
  }

  def createTileKmzInfo(def rasterEntry, def params)
  {
    def fullResBound = createFullResBounds( rasterEntry )
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"
    def tileBounds = tileBound( params, fullResBound )
    def wmsRequest = new WmsCommand()
    Utility.simpleCaseInsensitiveBind( wmsRequest, params )

    def newParams = new HashMap( params )
    newParams.remove( "action" )
    newParams.remove( "controller" )

    def edgeTileFlag = isAnEdgeTile( rasterEntry, fullResBound, params.level as Integer, params.row as Integer, params.col as Integer )
    def format = "image/jpeg"
    def transparent = false
    def ext = "jpg"
    if ( edgeTileFlag )
    {
      format = "image/png"
      transparent = true
      ext = "png"
    }
    Utility.simpleCaseInsensitiveBind( wmsRequest, [request: 'GetMap',
        layers: params.id,
        srs: 'EPSG:4326',
        format: format,
        request: "GetMap",
        version: "1.1.1",
        service: 'wms',
        width: tileSize.width,
        height: tileSize.height,
        transparent: transparent,
        bbox: "${tileBounds.minx},${tileBounds.miny},${tileBounds.maxx},${tileBounds.maxy}"] )
    def wmsMap = wmsRequest.toMap()
    Utility.removeEmptyParams( wmsMap )

    //def minLod = Math.sqrt(tileSize.width*tileSize.height)
    //def maxLod = minLod

    def subtiles = []
    if ( canSplit( tileBounds, rasterEntry.metersPerPixel ) )
    {
      subtiles = generateSubTiles( params, fullResBound )
    }
    def defaultName = "${params.level}/${params.col}/${params.row}.kml"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
          name( defaultName )
          description()
          Style() {
            ListStyle( id: "hideChildren" ) {
              listItemType( "checkHideChildren" )
            }
          }
          Region() {
            Lod() {
              minLodPixels( lodValues.min )
              if ( subtiles.size() > 0 )
              {
                maxLodPixels( lodValues.max )
              }
              else
              {
                maxLodPixels( -1 )
              }
            }
            LatLonAltBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          GroundOverlay() {
            drawOrder( params.level )
            Icon() {
              href { mkp.yieldUnescaped( "images/image.${ext}" ) }
    // href{mkp.yieldUnescaped("<![CDATA[${appTagLib.createLink(absolute: true, controller: 'ogc', action: 'wms',params:wmsMap)}]]>")}
              viewRefreshMode( "onExpire" )
            }
            LatLonBox() {
              north( tileBounds.maxy )
              south( tileBounds.miny )
              east( tileBounds.maxx )
              west( tileBounds.minx )
            }
          }
          subtiles.each { tile ->
            newParams.level = tile.level
            newParams.row = tile.row
            newParams.col = tile.col
            NetworkLink {
              name( "${tile.level}/${tile.col}/${tile.row}.${ext}" )
              Region {
                Lod {
                  minLodPixels( lodValues.min )
                  maxLodPixels( "-1" )
                }
                LatLonAltBox {
                  north( "${tile.maxy}" )
                  south( "${tile.miny}" )
                  east( "${tile.maxx}" )
                  west( "${tile.minx}" )
                }
              }
              Link {
                href {
                  mkp.yieldUnescaped( """<![CDATA[${
                    grailsLinkGenerator.link( absolute: true,
                        action: params.action, params: newParams )
                  }]]>""" )
                }
                viewRefreshMode( "onExpire" )
              }
            }
          }
        }
      }
    }
    kmlbuilder.bind( kmlnode ).toString()
    def mapResult = [image: null, errorMessage: null]
    if ( !rasterEntry )
    {
      mapResult.image = new BufferedImage( tileSize.width, tileSize.height, BufferedImage.TYPE_INT_RGB )
    }
    else
    {
      mapResult = webMappingService.getMap( wmsRequest, [rasterEntry] )
    }

    [kml: kmlbuilder.bind( kmlnode ).toString(), image: mapResult.image, format: "${ext}", imagePath: "images/image.${ext}"]
  }

  def isAnEdgeTile(def rasterEntry, def fullResBbox, def level, def row, def col)//def level, def row, def col)
  {
    // we will consider edge tiles as all tiles overlapping the bounds of the raster entry
    //
    def rasterGeom = rasterEntry.ground_geom.g
    def tileGeometry = createPolygonFromTileBounds( tileBound( level, row, col, fullResBbox ) )

    def result = !rasterGeom.contains( tileGeometry )
    result
  }

  def generateSubTiles(def params, def fullResBbox)
  {
    def level = ( params.level as Integer ) + 1
    def row = params.row as Integer
    def col = params.col as Integer
    def nrow = row * 2
    def ncol = col * 2
    def minx = fullResBbox.minx
    def maxx = fullResBbox.maxx
    def miny = fullResBbox.miny
    def maxy = fullResBbox.maxy
    def deltax = ( maxx - minx ) / ( 2**level )
    def deltay = ( maxy - miny ) / ( 2**level )

    def llx = minx + deltax * ncol
    def lly = miny + deltay * nrow

    [[minx: llx, miny: lly, maxx: ( llx + deltax ), maxy: ( lly + deltay ), level: level, col: ncol, row: nrow],
        [minx: llx + deltax, miny: lly, maxx: ( llx + 2.0 * deltax ), maxy: ( lly + deltay ), level: level, col: ( ncol + 1 ), row: nrow],
        [minx: llx + deltax, miny: ( lly + deltay ), maxx: ( llx + 2.0 * deltax ), maxy: ( lly + 2.0 * deltay ), level: level, col: ( ncol + 1 ), row: ( nrow + 1 )],
        [minx: llx, miny: lly + deltay, maxx: ( llx + deltax ), maxy: ( lly + 2.0 * deltay ), level: level, col: ncol, row: ( nrow + 1 )]
    ]
  }

    def getFeaturesKml(wmsParams, features) {
        def wmsBaseUrl = grailsApplication.config.omar.wms.baseUrl + "/wms?"

        def kmlNode = {
            mkp.xmlDeclaration()
            kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
                Document() {

                    Style( "id": "default" ) {
                        LineStyle() {
                            color( "ffffffff" )
                            width( 2 )
                        }
                        PolyStyle() { color( "00ffffff" ) }
                    }
                    Style( "id": "msi" ) {
                        LineStyle() {
                            color( "ff0000ff" )
                            width( 2 )
                        }
                        PolyStyle() { color( "000000ff" ) }
                    }
                    Style( "id": "vis" ) {
                        LineStyle() {
                            color( "ff00ffff" )
                            width( 2 )
                        }
                        PolyStyle() { color( "0000ffff" ) }
                    }

                    Folder() {
                        name( "Images" )
                        features.eachWithIndex() { value, index ->
                            def feature = value
                            GroundOverlay() {
                                description { mkp.yieldUnescaped( "<![CDATA[${getKmlDescription(feature)}]]>" ) }
                                name( "${index + 1}: " + (feature.title ?: feature.filename) )

                                Icon() {
                                    def wmsUrl = wmsBaseUrl
                                    wmsParams.FILTER = "in(${feature.get("id")})"
                                    wmsParams.each() { wmsUrl += "${it.key}=${it.value}&" }
                                    href { mkp.yieldUnescaped( "<![CDATA[${wmsUrl}]]>" ) }
                                    viewBoundScale( 0.85 )
                                    viewFormat(
                                        "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&" + "WIDTH=[horizPixels]&HEIGHT=[vertPixels]"
                                    )
                                    viewRefreshMode( "onStop" )
                                    viewRefreshTime( 1 )
                                }

                                LookAt() {
                                    def bounds = feature.ground_geom.envelopeInternal
                                    def centerLon = ( bounds?.minX + bounds?.maxX ) * 0.5
                                    def centerLat = ( bounds?.minY + bounds?.maxY ) * 0.5

                                    altitude( 0 )
                                    altitudeMode( "clampToGround" )
                                    heading( 0 )
                                    latitude( centerLat )
                                    longitude( centerLon )
                                    range( 15000 )
                                    tilt( 0 )
                                }

                                Snippet()
                                visibility( 0 )
                            }
                        }
                        open( 1 )
                    }

                    Folder() {
                        name( "Footprints" )
                        features.eachWithIndex() { value, index ->
                            def feature = value
                            Placemark() {
                                name( "${index + 1}: " + (feature.title ?: feature.filename) )
                                description { mkp.yieldUnescaped( "<![CDATA[${getKmlDescription(feature)}]]>" ) }

                                def bounds = feature.ground_geom.envelopeInternal
                                def centerLon = ( bounds?.minX + bounds?.maxX ) * 0.5
                                def centerLat = ( bounds?.minY + bounds?.maxY ) * 0.5

                                LookAt() {
                                    altitude( 0 )
                                    altitudeMode( "clampToGround" )
                                    heading( 0 )
                                    latitude( centerLat )
                                    longitude( centerLon )
                                    range( 15000 )
                                    tilt( 0 )
                                }

                                // the footprint geometry
                                mkp.yieldUnescaped( feature.ground_geom.getKml() )

                                Snippet()

                                switch (feature.sensor_id) {
                                    case "msi": styleUrl( "#msi" ); break
                                    case "vis": styleUrl( "#vis" ); break
                                    default: styleUrl( "#default" ); break
                                }

                            }
                        }
                        open( 1 )
                    }
                    open( 1 )
                }
            }
        }

        def kmlWriter = new StringWriter()
        def kmlBuilder = new StreamingMarkupBuilder()
        kmlWriter << kmlBuilder.bind( kmlNode )


        return kmlWriter.buffer
    }

    def getKmlDescription( feature ) {
        def o2BaseUrl = grailsApplication.config.omar.o2.baseUrl
        def imageUrl = "${o2BaseUrl}/omar/#/mapOrtho?layers=${feature.get("id")}"

        def wfsUrl = "${grailsApplication.config.omar.wfs.baseUrl}/wfs/getFeature?" +
            "filter=in(${feature.get("id")})&" +
            "request=GetFeature&" +
            "service=WFS&&" +
            "typeName=omar%3Araster_entry&" +
            "version=1.1.0"

        def tableMap = [
            "Acquistion Date": feature.acquisition_date ?: "",
            "Azimuth Angle": feature.azimuth_angle ?: "",
            "Bit Depth": feature.bit_depth ?: "",
            "Cloud Cover": feature.cloud_cover ?: "",
            "Country Code": feature.country_code ?: "",
            "Filename": "<a href = '${imageUrl}'>${feature.filename}</a>",
            "Grazing Angle": feature.grazing_angle ?: "",
            "GSD X/Y": (feature.gsdx && feature.gsdy) ? "${feature.gsdx} / ${feature.gsdy}" : "",
            "Image ID": feature.image_id ?: (feature.title ?: ""),
            "Ingest Date": feature.ingest_date ?: "",
            "NIIRS": feature.niirs ?: "",
            "# of Bands": feature.number_of_bands ?: "",
            "Security Class.": feature.security_classification ?: "",
            "Sensor": feature.sensor_id ?: "",
            "Sun Azimuth": feature.sun_azimuth ?: "",
            "Sun Elevation": feature.sun_elevation ?: "",
            "WFS": "<a href = '${wfsUrl}'>All Metadata</a>"
        ]

        def description = "<table style = 'width: auto; white-space: nowrap'>"
        tableMap.each() {
            description += "<tr>"
            description += "<th align = 'right'>${it.key}:</th>"
            description += "<td>${it.value}</td>"
            description += "</tr>"
        }

        description += "<tfoot><tr><td colspan='2'>"
        description +=     "<a href = '${o2BaseUrl}'>"
        description +=         "<img src = '${o2BaseUrl}/assets/o2-logo.png'/>"
        description +=     "</a>"
        description += "</td></tr></tfoot>"
        description += "</table>"


        return description
    }

    def getKmlWmsParams(params) {
        return [
            FORMAT: "image/png",
            LAYERS: "omar:raster_entry",
            REQUEST: "GetMap",
            SERVICE: "WMS",
            SRS: "EPSG:4326",
            TRANSPARENT: true,
            VERSION: "1.1.1"
        ]
    }

  def getMetersPerPixel(def tileBounds, def fullResMetersPerPixel)
  {
    def deltax = ( tileBounds.maxx - tileBounds.minx )
    def deltay = ( tileBounds.maxy - tileBounds.miny )
    //def maxDelta = deltax>deltay?deltay:deltax
    // def maxTileSize = tileSize.width>tileSize.height?tileSize.width:tileSize.height
    //def metersPerPixel = (maxDelta*metersPerDegree)/maxTileSize
    def metersPerPixel = ( ( ( deltax * metersPerDegree ) / tileSize.width ) +
        ( ( deltay * metersPerDegree ) / tileSize.height ) ) * 0.5

    metersPerPixel
  }

    def getLastImagesKml() {
        def kmlBuilder = new StreamingMarkupBuilder()
        kmlBuilder.encoding = "UTF-8"

        def kmlQueryUrl = grailsLinkGenerator.link(
            absolute: true, action: "kmlQuery",
            controller: "superOverlay", params: [maxFeatures: 10]
        )
        def kmlNode = {
            mkp.xmlDeclaration()
            kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
                NetworkLink() {
                    Link() {
                        href {
                            mkp.yieldUnescaped( "<![CDATA[${kmlQueryUrl}]]>" )
                        }
                        viewFormat( "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]" )
                        viewRefreshMode( "onRequest" )
                        viewRefreshTime( 0 )
                    }
                    name( "O2 Last 10 Images For View" )
                }
            }
        }


        return kmlBuilder.bind( kmlNode ).toString()
    }

    def kmlQuery(params) {
        // make sure the BBOX is valid
        def bbox = params.BBOX?.split(",").collect({ it as Double })
        if ( bbox ) {
            if ( bbox[0] < -180 ) { bbox[0] = -180 }
            if ( bbox[1] < -90 ) { bbox[1] = -90 }
            if ( bbox[2] > 180 ) { bbox[2] = 180 }
            if ( bbox[3] > 90 ) { bbox[3] = 90 }
        }
        else { bbox = [-180, -90, 180, 90] }

        def polygon = new Bounds(bbox[0], bbox[1], bbox[2], bbox[3]).createRectangle(4, 0)
        def filter = "INTERSECTS(ground_geom,${polygon})"

        // limit the number of possible returns
        def maxFeatures = params.maxFeatures ?: 10
        if (!maxFeatures.toString().isNumber()) { maxFeatures = 10 }
        else { maxFeatures = maxFeatures as Integer }
        if (maxFeatures > 100) { maxFeatures = 100 }

        // conduct a search for imagery
        def wfsParams = [
            filter: filter,
            maxFeatures: maxFeatures,
            typeName: "omar:raster_entry"
        ]
        def layerInfo = geoscriptService.findLayerInfo( wfsParams )
        def options = geoscriptService.parseOptions( wfsParams )

        def features
        Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->
            def layer = workspace[layerInfo.name]
            features = layer.collectFromFeature( options ) { feature -> return feature }
            workspace.close()
        }

        def wmsParams = getKmlWmsParams(params)
        def kml = getFeaturesKml(wmsParams, features)


        return kml
    }

  def tileBound(def params, def fullResBbox)
  {
    tileBound( params.level ? params.level as Integer : 0,
        params.row ? params.row as Integer : 0,
        params.col ? params.col as Integer : 0,
        fullResBbox )
  }

  def tileBound(def level, def row, def col, def fullResBbox)
  {
    def minx = fullResBbox.minx
    def maxx = fullResBbox.maxx
    def miny = fullResBbox.miny
    def maxy = fullResBbox.maxy
    def deltax = ( maxx - minx ) / ( 2**level )
    def deltay = ( maxy - miny ) / ( 2**level )

    def llx = minx + deltax * col
    def lly = miny + deltay * row

    [minx: llx, miny: lly, maxx: ( llx + deltax ), maxy: ( lly + deltay )]
  }

  void afterPropertiesSet()
  {
    def gpt = new ossimGpt()
    def dpt = gpt.metersPerDegree()
    metersPerDegree = dpt.y
    dpt.delete()
    gpt.delete()
    dpt = null
    gpt = null
    tileSize = grailsApplication.config.superOverlay?.tileSize
    lodValues = grailsApplication.config.superOverlay?.lodPixel
    tileSize = tileSize ?: [width: 256, height: 256]
    def square = Math.sqrt( tileSize.width * tileSize.height )
    lodValues = lodValues ?: [min: square * 0.5, max: square * 8.0]
  }
}
