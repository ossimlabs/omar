package omar.wms

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper
import groovy.xml.StreamingMarkupBuilder

import geoscript.workspace.Workspace
import geoscript.geom.Bounds
import geoscript.proj.Projection
import geoscript.render.Map as GeoScriptMap

import omar.geoscript.LayerInfo
import omar.geoscript.WorkspaceInfo
import org.geotools.data.DataStoreFinder
import org.geotools.referencing.CRS
import org.springframework.beans.factory.InitializingBean

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Slf4j
class WebMappingService implements InitializingBean
{
  static transactional = false

  def grailsLinkGenerator
  def grailsApplication
  def geoscriptService

  def serverData
//  def layers
  def projections

  @Override
  void afterPropertiesSet() throws Exception
  {
    serverData = grailsApplication.config.geoscript.serverData
//    layers = grailsApplication.config.geoscript.layers

    //projections = CRS.getSupportedAuthorities( false ).inject( [] ) { a, b ->
    projections = ['AUTO', 'EPSG', 'CRS'].inject( [] ) { a, b ->
      def c = CRS.getSupportedCodes( b )?.grep( ~/\d+/ )?.collect { it?.toInteger() }?.sort()
      def d = c?.collect { "${b}:${it}" }

      a.addAll( d )
      a
    }
  }

  enum RenderMode {
    BLANK, GEOSCRIPT, FILTER
  }

  def getCapabilities(GetCapabilitiesRequest wmsParams)
  {
    def contentType, buffer
    def version = wmsParams?.version ?: "1.3.0"
    def schemaLocation = grailsLinkGenerator.link( absolute: true, uri: "/schemas/wms/1.3.0/capabilities_1_3_0.xsd" )
    def docTypeLocation = grailsLinkGenerator.link( absolute: true, uri: "/schemas/wms/1.1.1/WMS_MS_Capabilities.dtd" )

    def x = {
      mkp.xmlDeclaration()

      if ( version == "1.1.1" )
      {
        mkp.yieldUnescaped """<!DOCTYPE WMT_MS_Capabilities SYSTEM "${docTypeLocation}">"""
      }

      def rootTag = ( version == "1.1.1" ) ? "WMT_MS_Capabilities" : "WMS_Capabilities"
      def rootAttributes = [version: version]

      mkp.declareNamespace(
          xlink: "http://www.w3.org/1999/xlink",
      )

      if ( version == "1.3.0" )
      {
        mkp.declareNamespace(
            xsi: "http://www.w3.org/2001/XMLSchema-instance"
        )

        rootAttributes['xmlns'] = "http://www.opengis.net/wms"
        rootAttributes['xsi:schemaLocation'] = "http://www.opengis.net/wms ${schemaLocation}"
      }

      "${rootTag}"( rootAttributes ) {

        Service {
          Name( serverData.Service.Name )
          Title( serverData.Service.Title )
          Abstract( serverData.Service.Abstract )
          KeywordList {
            serverData.Service.KeywordList.each { keyword ->
              Keyword( keyword )
            }
          }
          OnlineResource( 'xlink:type': "simple", 'xlink:href': serverData.Service.OnlineResource )
          ContactInformation {
            ContactPersonPrimary {
              ContactPerson( serverData.Service.ContactInformation.ContactPersonPrimary.ContactPerson )
              ContactOrganization( serverData.Service.ContactInformation.ContactPersonPrimary.ContactOrganization )
            }
            ContactPosition( serverData.Service.ContactInformation.ContactPosition )
            ContactAddress {
              AddressType( serverData.Service.ContactInformation.ContactAddress.AddressType )
              Address( serverData.Service.ContactInformation.ContactAddress.Address )
              City( serverData.Service.ContactInformation.ContactAddress.City )
              StateOrProvince( serverData.Service.ContactInformation.ContactAddress.StateOrProvince )
              PostCode( serverData.Service.ContactInformation.ContactAddress.PostCode )
              Country( serverData.Service.ContactInformation.ContactAddress.Country )
            }
            ContactVoiceTelephone( serverData.Service.ContactInformation.ContactVoiceTelephone )
            ContactFacsimileTelephone( serverData.Service.ContactInformation.ContactFacsimileTelephone )
            ContactElectronicMailAddress( serverData.Service.ContactInformation.ContactElectronicMailAddress )
          }
          Fees( serverData.Service.Fees )
          AccessConstraints( serverData.Service.AccessConstraints )
        }
        Capability {
          Request {
            GetCapabilities {
              contentType = ( version == '1.1.1' ) ? "application/vnd.ogc.wms_xml" : "text/xml"
              Format( contentType )
              DCPType {
                HTTP {
                  Get {
                    OnlineResource( 'xlink:type': "simple",
                        'xlink:href': grailsLinkGenerator.link( absolute: true, controller: 'wms', action: 'getCapabilities' ) )
                  }
                  Post {
                    OnlineResource( 'xlink:type': "simple",
                        'xlink:href': grailsLinkGenerator.link( absolute: true, controller: 'wms', action: 'getCapabilities' ) )
                  }
                }
              }
            }
            GetMap {
              serverData.Capability.Request.GetMap.Format.each { format ->
                Format( format )
              }
              DCPType {
                HTTP {
                  Get {
                    OnlineResource( 'xlink:type': "simple",
                        'xlink:href': grailsLinkGenerator.link( absolute: true, controller: 'wms', action: 'getMap' ) )
                  }
                }
              }
            }
/*
            GetFeatureInfo {
              serverData.Capability.Request.GetFeatureInfo.Format.each { format ->
                Format( format )
              }
              DCPType {
                HTTP {
                  Get {
                    OnlineResource( 'xlink:type': "simple",
                        'xlink:href': grailsLinkGenerator.link( absolute: true, controller: 'wms', action: 'getFeatureInfo' ) )
                  }
                }
              }
            }
*/
          }
          Exception {
            serverData.Capability.Exception.Format.each { format ->
              Format( format )
            }
          }
          Layer {
            Title( serverData.Capability.Layer.Title )
            Abstract( serverData.Capability.Layer.Abstract )
            def crsTag = ( version == '1.1.1' ) ? "SRS" : "CRS"
            projections?.each { crs ->
              "${crsTag}"( crs )
            }
            if ( version == '1.3.0' )
            {
              EX_GeographicBoundingBox {
                westBoundLongitude( serverData.Capability.Layer.BoundingBox.minLon )
                eastBoundLongitude( serverData.Capability.Layer.BoundingBox.maxLon )
                southBoundLatitude( serverData.Capability.Layer.BoundingBox.minLat )
                northBoundLatitude( serverData.Capability.Layer.BoundingBox.maxLat )
              }
              BoundingBox( CRS: serverData.Capability.Layer.BoundingBox.crs,
                  minx: serverData.Capability.Layer.BoundingBox.minLon,
                  miny: serverData.Capability.Layer.BoundingBox.minLat,
                  maxx: serverData.Capability.Layer.BoundingBox.maxLon,
                  maxy: serverData.Capability.Layer.BoundingBox.maxLat
              )
            }
            else
            {
              LatLonBoundingBox(
                  minx: serverData.Capability.Layer.BoundingBox.minLon,
                  miny: serverData.Capability.Layer.BoundingBox.minLat,
                  maxx: serverData.Capability.Layer.BoundingBox.maxLon,
                  maxy: serverData.Capability.Layer.BoundingBox.maxLat
              )
            }
            LayerInfo.list()?.each { layerInfo ->
              WorkspaceInfo workspaceInfo = WorkspaceInfo.findByName( layerInfo.workspaceInfo.name )

              Workspace.withWorkspace( geoscriptService.getWorkspace( workspaceInfo?.workspaceParams ) ) { Workspace workspace ->
                try
                {
                  def layer = workspace[layerInfo.name]
                  def bounds = layer.bounds
                  def geoBounds = ( layer?.proj?.epsg == 4326 ) ? bounds : bounds?.reproject( 'epsg:4326' )

//                Layer( queryable: layerInfo?.queryable, opaque: layerInfo?.opaque ?: "0" ) {
                  Layer( queryable: "1", opaque: "0" ) {

                    Name( "${layerInfo.workspaceInfo.namespaceInfo.prefix}:${layerInfo.name}" )
                    Title( layerInfo?.title )
                    Abstract( layerInfo?.description )
                    if ( layerInfo?.keywords )
                    {
                      KeywordList {
                        layerInfo?.keywords?.each { keyword ->
                          Keyword( keyword )
                        }
                      }
                    }
                    "${crsTag}"( bounds?.proj?.id )
                    if ( version == "1.3.0" )
                    {
                      EX_GeographicBoundingBox {
                        westBoundLongitude( geoBounds?.minX )
                        eastBoundLongitude( geoBounds?.maxX )
                        southBoundLatitude( geoBounds?.minY )
                        northBoundLatitude( geoBounds?.maxY )
                      }
                    }
                    else
                    {
                      LatLonBoundingBox(
                          minx: geoBounds?.minX,
                          miny: geoBounds?.minY,
                          maxx: geoBounds?.maxX,
                          maxy: geoBounds?.maxY
                      )
                    }
                    BoundingBox( ( "${crsTag}" ): bounds?.proj?.id,
                        minx: bounds?.minX, miny: bounds?.minY,
                        maxx: bounds?.maxX, maxy: bounds?.maxY )
//                  layerInfo?.styles?.each { style ->
/*
                    [].each { style ->
                      Style {
                        Name( style?.name )
                        Title( style?.title )
                        Abstract( style?.description )

                        LegendURL( width: style?.legend?.width, height: style?.legend?.height ) {
                          Format( style?.legend?.format )
                          OnlineResource( 'xmlns:xlink': "http://www.w3.org/1999/xlink", 'xlink:type': "simple",
                              'xlink:href': style?.legend?.url )
                        }
                      }
                    }
*/
                  }
                }
                catch ( e )
                {
                  e.printStackTrace()
                }
              }
            }
          }
        }
      }
    }

    buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x )?.toString()?.trim()

    [contentType: contentType, buffer: buffer]
  }


  def getMap(GetMapRequest wmsParams)
  {
    log.trace "getMap: Entered ................"
    def renderMode = RenderMode.FILTER
    def otherParams = [startDate: new Date()
                       ]
    otherParams.startTime = System.currentTimeMillis()
    otherParams.internalTime = otherParams.startTime

//    println wmsParams

    def ostream = new ByteArrayOutputStream()
    def style = [:]

    if (wmsParams?.styles?.trim()) {
      try {
        style = new JsonSlurper().parseText(wmsParams?.styles)
      } catch ( e ) {
        e.printStackTrace()
      }
    }

    //println "style: ${style}"

    switch ( renderMode )
    {
    case RenderMode.GEOSCRIPT:
      log.trace "getMap: Using  RenderMode.GEOSCRIPT Method"
      def images = wmsParams?.layers?.split( ',' )?.collect { [imageFile: it.toString()] }
      def chipperLayer = new ChipperLayer( images, style )

      def map = new GeoScriptMap(
          fixAspectRatio: false,
          width: wmsParams?.width,
          height: wmsParams?.height,
          type: wmsParams?.format?.split( '/' )?.last(),
          proj: wmsParams?.srs,
          bounds: new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() } ), wmsParams?.srs ),
          layers: [chipperLayer]
      )

      map.render( ostream )
      map.close()
      otherParams.internalTime = System.currentTimeMillis()
      break

    case RenderMode.BLANK:
      log.trace "getMap: Using  RenderMode.BLANK Method"
      def image = new BufferedImage( wmsParams.width, wmsParams.height, BufferedImage.TYPE_INT_ARGB )

      ImageIO.write( image, wmsParams?.format?.split( '/' )?.last(), ostream )
      break

    case RenderMode.FILTER:
      log.trace "getMap: Using  RenderMode.FILTER Method"

      def layerNames = wmsParams?.layers?.split( ',' )
      def layers = []


      layerNames?.each { layerName ->
        def parts = layerName?.split( /[:\.]/ )

        def prefix, typeName, id

        switch ( parts?.size() )
        {
        case 2:
          (prefix, typeName) = parts
          break
        case 3:
          (prefix, typeName, id) = parts
          break
        }

//        println "${prefix} ${typeName} ${id}"

        def layerInfo = LayerInfo.where {
          name == typeName && workspaceInfo.namespaceInfo.prefix == prefix
        }.get()
//          println "LAYER INFO ============= ${layerInfo}"
        List images = null

        //def maxCount = grailsApplication?.config.omar.wms.autoMosaic.maxCount
        //println "BEFORE: ${maxCount}"
        //maxCount = maxCount?:10
        //println maxCount
        //def sorting = grailsApplication?.config.omar.wms.autoMosaic.sorting
        HashMap workspaceParams = layerInfo.workspaceInfo.workspaceParams

        Workspace.withWorkspace( geoscriptService.getWorkspace( workspaceParams ) ) { Workspace workspace ->
          def layer = workspace[typeName]

          images = layer?.collectFromFeature(
              filter: ( id ) ? "in(${id})" : wmsParams?.filter,
          //sorting: sorting,
          //	max: maxCount, // will remove and change to have the wms plugin have defaults
              fields: ['id', 'ground_geom', 'filename', 'entry_id'] as List<String>
          ) {
            [id: it.get( 'id' ), imageFile: it.filename, groundGeom: it.ground_geom, entry: it.entry_id?.toInteger()]
          }
        }

        // apply ordering for filters having:  in(1,2,3)
        def ids = wmsParams?.filter?.find( /.*in[(](.*)[)].*/ ) { matcher, ids -> return ids }
        if ( ids )
        {
          def orderedImages = []
          ids.split( "," ).collect( { it as Integer } ).each() {
            def idIndex = it
            orderedImages << images.find { it.id == idIndex }
          }
          images = orderedImages
        }

        def chipperLayer = new ChipperLayer( images, style )

        layers << chipperLayer
      }

      def coords = wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() }
      def proj = new Projection( ( wmsParams.version == "1.3.0" ) ? wmsParams?.crs : wmsParams?.srs )
      def bbox

      if ( wmsParams.version == "1.3.0" && proj?.crs?.unit?.toString() == '\u00b0' )
      {
        bbox = new Bounds( coords[1], coords[0], coords[3], coords[2], proj )
      }
      else
      {
        bbox = new Bounds( *coords, proj )
      }

      def renderParams = [
          fixAspectRatio: false,
          width: wmsParams?.width,
          height: wmsParams?.height,
          type: wmsParams?.format?.split( '/' )?.last(),
          proj: bbox?.proj,
          bounds: bbox,
          layers: layers
      ]

      def map = new GeoScriptMap( renderParams )

      map.render( ostream )
      map.close()
      otherParams.internalTime = System.currentTimeMillis()
      break
    }

    //otherParams.endDate = new Date()

    log.trace "getMap: Leaving ................"
    [contentType: wmsParams.format, buffer: ostream.toByteArray(), metrics: otherParams]
  }
}
