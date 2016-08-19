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
  def geoscriptService

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
//    println wcsParams

    def contentType
    def buffer

    try
    {
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

          def layers

          try
          {
            layers = getLayers( wcsParams )
          }
          catch ( e )
          {
            contentType = 'application/vnd.ogc.se_xml'
            buffer = createErrorMessage( e )
            return [contentType: contentType, buffer: buffer]
          }

          wcs.ContentMetadata {
            layers?.each { layer ->
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

      contentType = 'application/xml'
      buffer = xml

    }
    catch ( e )
    {
      contentType = 'application/vnd.ogc.se_xml'
      buffer = createErrorMessage( e )
    }

    [contentType: contentType, buffer: buffer]
  }

  private def getLayers(def wcsParams) throws Exception
  {
    def coverages = wcsParams?.coverage?.split( ',' )*.split( /[:\.]/ )
    def images = []

    coverages?.each { coverage ->
//      println coverage
      def prefix, layerName, id

      if ( coverage.size() == 3 )
      {
        (prefix, layerName, id) = coverage
      }
      else if ( coverage.size() == 2 )
      {
        (prefix, layerName) = coverage
      }

      if ( layerName == null || prefix == null )
      {
        throw new Exception( "Unknown coverage:  ${prefix}:${layerName}" )
      }

      def layerInfo = LayerInfo.where {
        name == layerName && workspaceInfo.namespaceInfo.prefix == prefix
      }.get()

      Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->
        def layer = workspace[layerName]

        if ( id )
        {
          def image = layer?.getFeatures( filter: "in (${id})" )?.first()

          if ( image )
          {
            images << convertImage( prefix, image )
          }
        }
        else if ( wcsParams.filter )
        {
          layer?.eachFeature( filter: wcsParams.filter ) { images << convertImage( prefix, it ) }
        }
      }
    }

    images
  }

  private def convertImage(def prefix, def image)
  {

    def bounds = image.ground_geom.bounds
    def title = image.title ?: image.image_id ?: ( image.filename as File )?.name

    def metadata = [
        label: title,
        description: image.description,
        name: "${prefix}:${image.id}",
        bounds: [
            //srsName: "urn:ogc:def:crs:OGC:1.3:CRS84",
            proj: "EPSG:4326",
            minX: bounds.minX, minY: bounds.minY, maxX: bounds.maxX, maxY: bounds.maxY
        ],
        keywords: ["WCS"],
        width: image.width, height: image.height,
        numBands: image.number_of_bands,
        filename: image.filename,
        entry: image.entry_id?.toInteger()
    ]

//    println metadata
    metadata
  }

  def describeCoverage(DescribeCoverageRequest wcsParams)
  {
//    println wcsParams

    def contentType
    def buffer

    try
    {
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

        def layers = getLayers( wcsParams )

//      println layer

        wcs.CoverageDescription( version: "1.0.0",
            'xsi:schemaLocation': "http://www.opengis.net/wcs ${schemaLocation}" ) {
          layers.each { layer ->

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
      }

      def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

      contentType = 'application/xml'
      buffer = xml
    }
    catch ( e )
    {
      contentType = 'application/vnd.ogc.se_xml'
      buffer = createErrorMessage( e )
    }

    [contentType: contentType, buffer: buffer]
  }

  def getCoverage(GetCoverageRequest wcsParams)
  {
//    println wcsParams

    def contentType
    def buffer

    try
    {
      def coverageLayers = getLayers( wcsParams )?.collect { coverage ->
        new ChipperLayer( coverage.filename as File, coverage.entry )
      }

      def viewBbox = new Bounds( *( wcsParams?.bbox?.split( ',' )*.toDouble() ), wcsParams.crs )
      def coverageBbox = coverageLayers?.first()?.bbox

      coverageLayers?.each { coverageBbox = coverageBbox.expand( it.bbox ) }

      def bbox = viewBbox.intersection( coverageBbox )

//    println bbox

      def map = new GeoScriptMap(
          fixAspectRatio: false,
          type: wcsParams?.format?.toLowerCase(),
          width: wcsParams?.width,
          height: wcsParams?.height,
          proj: bbox.proj,
          bounds: bbox,
          layers: coverageLayers
      )

      def ostream = new ByteArrayOutputStream()

      map.renderers['geotiff'] = new GeoTIFF()
      map?.render( ostream )
      map?.close()

      contentType = 'image/tiff'
      buffer = ostream.toByteArray()
    }
    catch ( e )
    {
      contentType = 'application/vnd.ogc.se_xml'
      buffer = createErrorMessage( e )?.bytes
    }


    [contentType: contentType, buffer: buffer]
  }

  private createErrorMessage(Exception e)
  {
    def schemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wcs/1.0.0/OGC-exception.xsd' )

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          xsi: "http://www.w3.org/2001/XMLSchema-instance"
      )
      ServiceExceptionReport(
          version: "1.2.0",
          xmlns: "http://www.opengis.net/ogc",
          'xsi:schemaLocation': schemaLocation
      ) {
        ServiceException( e.message )
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    xml.toString()
  }
}
