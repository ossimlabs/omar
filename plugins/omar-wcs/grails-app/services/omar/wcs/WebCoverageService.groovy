package omar.wcs

import geoscript.filter.Filter
import geoscript.workspace.Workspace
import grails.transaction.Transactional
import groovy.xml.StreamingMarkupBuilder

import geoscript.geom.Bounds
import geoscript.layer.Format
import geoscript.layer.Shapefile
import geoscript.render.Map as GeoScriptMap
import geoscript.style.RasterSymbolizer
import omar.geoscript.LayerInfo
import org.geotools.map.GridReaderLayer

@Transactional( readOnly = true )
class WebCoverageService
{
  def grailsLinkGenerator
  def grailsApplication

  static final supportedFormats = [
      "GeoTIFF", //"GIF", "PNG", "TIFF"
  ]

  static final requestResponseCRSs = [
      "EPSG:4326"
  ]

  static final supportedInterpolations = [
      "nearest neighbor"
  ]

  static final defaultInterpolation = "nearest neighbor"

  def getCapabilities(GetCapabilitiesRequest wcsParams)
  {
    println wcsParams

    def schemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wcs/1.0.0/wcsCapabilities.xsd' )
    def wcsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wcs' )

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          gml: "http://www.opengis.net/gml",
          ogc: "http://www.opengis.net/ogc",
          ows: "http://www.opengis.net/ows/1.1",
          wcs: "http://www.opengis.net/wcs",
          xlink: "http://www.w3.org/1999/xlink",
          xsi: "http://www.w3.org/2001/XMLSchema-instance"
      )
      wcs.WCS_Capabilities(
          version: "1.0.0",
          'xsi:schemaLocation': "http://www.opengis.net/wcs ${schemaLocation}"
      ) {
        wcs.Service {
          wcs.metadataLink( about: "http://o2.ossim.org",
              metadataType: "other", 'xlink:type': "simple" )
          wcs.description( "This server implements the WCS specification 1.0.0" )
          wcs.name( "WCS" )
          wcs.label( "Web Coverage Service" )
          wcs.keywords {
            wcs.keyword( "WCS" )
            wcs.keyword( "WMS" )
            wcs.keyword( "OMAR" )
          }
          wcs.responsibleParty {
            wcs.individualName()
            wcs.organisationName()
            wcs.positionName()
            wcs.contactInfo {
              wcs.phone()
              wcs.address {
                wcs.city()
                wcs.country()
                wcs.electronicMailAddress()
              }
            }
          }
          wcs.fees( "NONE" )
          wcs.accessConstraints( "NONE" )
        }
        wcs.Capability {
          wcs.Request {
            wcs.GetCapabilities {
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Get {
                    wcs.OnlineResource( 'xlink:href': "${wcsServiceAddress}" )
                  }
                }
              }
/*
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Post {
                    wcs.OnlineResource( 'xlink:href': wcsServiceAddress )
                  }
                }
              }
*/
            }
            wcs.DescribeCoverage {
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Get {
                    wcs.OnlineResource( 'xlink:href': "${wcsServiceAddress}" )
                  }
                }
              }
/*
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Post {
                    wcs.OnlineResource( 'xlink:href': wcsServiceAddress )
                  }
                }
              }
*/
            }
            wcs.GetCoverage {
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Get {
                    wcs.OnlineResource( 'xlink:href': "${wcsServiceAddress}" )
                  }
                }
              }
/*
              wcs.DCPType {
                wcs.HTTP {
                  wcs.Post {
                    wcs.OnlineResource( 'xlink:href': wcsServiceAddress )
                  }
                }
              }
*/
            }
          }
          wcs.Exception {
            wcs.Format( 'application/vnd.ogc.se_xml' )
          }
        }
        def layers = getLayers()

        wcs.ContentMetadata {
          layers.each { layer ->
            wcs.CoverageOfferingBrief {
              wcs.description( layer.description )
              wcs.name( layer.name )
              wcs.label( layer.label )
              wcs.lonLatEnvelope( srsName: layer.bounds.proj ) {
                gml.pos( "${layer.bounds.minX} ${layer.bounds.minY}" )
                gml.pos( "${layer.bounds.maxX} ${layer.bounds.maxY}" )
              }
              wcs.keywords {
                layer.keywords.each {
                  wcs.keyword( it )
                }
              }
            }
          }
        }
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    [contentType: 'application/xml', buffer: xml]
  }

  private def getLayer(def name)
  {
    getLayers().find { it.name.equalsIgnoreCase( name ) }
  }

  /*
  private def getLayers(def filter = Filter.PASS)
  {
    def layers = [[
//        name: "nurc:Arc_Sample",
//        description: "Generated from arcGridSample",
//        label: "A sample ArcGrid file",
//        bounds: [
//            proj: "urn:ogc:def:crs:OGC:1.3:CRS84",
//            minX: -180.0,
//            minY: -90.0,
//            maxX: 180.0,
//            maxY: 90.0
//        ],
//        keywords: [
//            "WCS", "arcGridSample", "arcGridSample_Coverage"
//        ]
//    ], [
            description: "A very rough imagery of North America",
            name: "nurc:Img_Sample",
            label: "North America sample imagery",
            bounds: [
                proj: "urn:ogc:def:crs:OGC:1.3:CRS84",
                minX: -130.85168,
                minY: 20.7052,
                maxX: -62.0054,
                maxY: 54.1141
            ],
            keywords: [
            "WCS", "worldImageSample", "worldImageSample_Coverage",
        ]
//    ], [
//        description: "Generated from ImageMosaic",
//        name: "nurc:mosaic",
//        label: "mosaic",
//        bounds: [
//            proj: "urn:ogc:def:crs:OGC:1.3:CRS84",
//            minX: 6.346,
//            minY: 36.492,
//            maxX: 20.83,
//            maxY: 46.591
//        ],
//        keywords: [
//            "WCS", "ImageMosaic", "mosaic"
//        ],
//    ], [
//        description: "Generated from sfdem",
//        name: "sf:sfdem",
//        label: "sfdem is a Tagged Image File Format with Geographic information",
//        bounds: [
//            proj: "urn:ogc:def:crs:OGC:1.3:CRS84",
//            minX: -103.87108701853181,
//            minY: 44.370187074132616,
//            maxX: -103.62940739432703,
//            maxY: 44.5016011535299
//        ],
//        keywords: [
//            "WCS", "sfdem", "sfdem"
//        ]

    ], [
        description: "San Fran",
        name: "o2:SanFranColor",
        label: "San Francisco",
        bounds: [
            //srsName: "urn:ogc:def:crs:OGC:1.3:CRS84",
            proj: "EPSG:4326",
            minX: -122.567038179736, minY: 37.6654930872174, maxX: -122.109265804213, maxY: 38.0233940932067
        ],
        keywords: ["WCS"],
        width: 8000, height: 8000,
        numBands: 3
    ]]
    layers
  }
  */

  def getLayers(def wcsParams)
  {
    def (prefix, layerName) = wcsParams?.coverage?.split( ':' )

    def layerInfo = LayerInfo.where {
      name == layerName && workspaceInfo.namespaceInfo.prefix == prefix
    }.get()

    List images = null

    Workspace.withWorkspace( layerInfo.workspaceInfo.workspaceParams ) { workspace ->
      def layer = workspace[layerName]

      images = layer.collectFromFeature(
          filter: wcsParams?.filter,
          //sorting: sorting,
          //	max: maxCount, // will remove and change to have the wms plugin have defaults
          fields: ['filename', 'entry_id'] as List<String>
      ) {
        [imageFile: it.filename as File, entry: it.entry_id?.toInteger()]
      }
    }

    images
  }

  def describeCoverage(DescribeCoverageRequest wcsParams)
  {
    println wcsParams

    def schemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wcs/1.0.0/describeCoverage.xsd' )

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          gml: "http://www.opengis.net/gml",
          ogc: "http://www.opengis.net/ogc",
          ows: "http://www.opengis.net/ows/1.1",
          wcs: "http://www.opengis.net/wcs",
          xlink: "http://www.w3.org/1999/xlink",
          xsi: "http://www.w3.org/2001/XMLSchema-instance"
      )

      def layer = getLayer( wcsParams.coverage )

//      println layer

      wcs.CoverageDescription( version: "1.0.0",
          'xsi:schemaLocation': "http://www.opengis.net/wcs ${schemaLocation}" ) {

        wcs.CoverageOffering {
          wcs.description( layer.description )
          wcs.name( layer.name )
          wcs.label( layer.label )

          wcs.lonLatEnvelope( srsName: layer.bounds.proj ) {
            gml.pos( "${layer.bounds.minX} ${layer.bounds.minY}" )
            gml.pos( "${layer.bounds.maxX} ${layer.bounds.maxY}" )
          }

          wcs.keywords {
            layer.keywords.each { wcs.keyword( it ) }
          }

          wcs.domainSet {
            wcs.spatialDomain {
              gml.Envelope( srsName: layer.bounds.proj ) {
                gml.pos( "${layer.bounds.minX} ${layer.bounds.minY}" )
                gml.pos( "${layer.bounds.maxX} ${layer.bounds.maxY}" )
              }
//              gml.RectifiedGrid( dimension: "2", srsName: layer.bbox.srsName ) {
//                gml.limits {
//                  gml.GridEnvelope {
//                    gml.low( "0 0" )
//                    gml.high( "${layer.width} ${layer.height}" )
//                  }
//                }
//                gml.axisName( "x" )
//                gml.axisName( "y" )
//                gml.origin {
//                  gml.pos( "-130.81666154628687 54.08616613712375" )
//                }
//                gml.offsetVector( "0.07003690742624616 0.0" )
//                gml.offsetVector( "0.0 -0.05586772575250837" )
//              }
            }
          }
          wcs.rangeSet {
            wcs.RangeSet {
              wcs.name( layer.name )
              wcs.label( layer.description )
              wcs.axisDescription {
                wcs.AxisDescription {
                  wcs.name( "Band" )
                  wcs.label( "Band" )
                  wcs.values {
                    wcs.interval {
                      wcs.min( 0 )
                      wcs.max( layer.numBands - 1 )
                    }
                  }
                }
              }
            }
          }
          wcs.supportedCRSs {
            requestResponseCRSs.each { wcs.requestResponseCRSs( it ) }
          }
          wcs.supportedFormats( /*nativeFormat: "WorldImage"*/ ) {
            supportedFormats.each { wcs.formats( it ) }
          }
          wcs.supportedInterpolations( default: defaultInterpolation ) {
            supportedInterpolations.each { wcs.interpolationMethod( it ) }
          }
        }
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    [contentType: 'application/xml', buffer: xml]
  }

  def getCoverage(GetCoverageRequest wcsParams)
  {
    println wcsParams

    def coverage = getLayer( wcsParams.coverage )

//    println coverage

    def bbox = new Bounds( *( wcsParams?.bbox?.split( ',' )*.toDouble() ), wcsParams.crs ).intersection(
        new Bounds( coverage.bounds.minX, coverage.bounds.minY, coverage.bounds.maxX, coverage.bounds.maxY,
            coverage.bounds.srsName )
    )

//    println bbox

    def map = new GeoScriptMap(
        fixAspectRatio: false,
        type: wcsParams?.format?.toLowerCase(),
        width: wcsParams?.width,
        height: wcsParams?.height,
        proj: bbox.proj,
        bounds: bbox,
        layers: [
            createLayer( coverage )
        ]
    )

    def ostream = new ByteArrayOutputStream()

    map.renderers['geotiff'] = new GeoTIFF()
    map?.render( ostream )
    map?.close()

    [contentType: 'image/tiff', buffer: ostream.toByteArray()]
  }


  private def createLayer(def coverage)
  {
    def layer

    switch ( coverage.name )
    {
    case 'nurc:Img_Sample':
      layer = new Shapefile( '/data/omar/world_adm0.shp' )
      break

    case 'o2:SanFranColor':
      def file = '/data/sanfran/sanfran_color.ccf' as File
//      def reader = Format.getFormat( file ).gridFormat.getReader( file )
//      layer = new GridReaderLayer( reader, new RasterSymbolizer().gtStyle )
      layer = new ChipperLayer( file )

      break
    }

    layer
  }
}
