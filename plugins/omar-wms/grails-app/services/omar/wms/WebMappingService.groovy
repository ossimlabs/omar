package omar.wms

import groovy.util.logging.Slf4j
import groovy.xml.StreamingMarkupBuilder

import geoscript.workspace.Workspace
import geoscript.geom.Bounds
import geoscript.render.Map as GeoScriptMap

import omar.geoscript.LayerInfo
import omar.geoscript.WorkspaceInfo

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Slf4j
class WebMappingService
{
	static transactional = false

	def grailsLinkGenerator
	def grailsApplication

	enum RenderMode {
		BLANK, GEOSCRIPT, FILTER
	}

	static final def supportedProjections = [
			'EPSG:4326', 'EPSG:3857'
	]

	static final def getMapOutputFormats = [
			'image/png',
//      'application/atom xml',
//      'application/atom+xml',
//      'application/openlayers',
//      'application/pdf',
//      'application/rss xml',
//      'application/rss+xml',
//      'application/vnd.google-earth.kml',
//      'application/vnd.google-earth.kml xml',
//      'application/vnd.google-earth.kml+xml',
//      'application/vnd.google-earth.kml+xml;mode=networklink',
//      'application/vnd.google-earth.kmz',
//      'application/vnd.google-earth.kmz xml',
//      'application/vnd.google-earth.kmz+xml',
//      'application/vnd.google-earth.kmz;mode=networklink',
//      'atom',
//      'image/geotiff',
//      'image/geotiff8',
			'image/gif',
//      'image/gif;subtype=animated',
			'image/jpeg',
//      'image/png8',
//      'image/png; mode=8bit',
//      'image/svg',
//      'image/svg xml',
//      'image/svg+xml',
//      'image/tiff',
//      'image/tiff8',
//      'kml',
//      'kmz',
//      'openlayers',
//      'rss',
//      'text/html; subtype=openlayers'
	]

	def getCapabilities( GetCapabilitiesRequest wmsParams )
	{
		def schemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wms/1.1.1/WMS_MS_Capabilities.dtd' )
		def wmsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wms' )

		def x = {
			mkp.xmlDeclaration()
			mkp.yieldUnescaped """
      <!DOCTYPE WMT_MS_Capabilities SYSTEM "${ schemaLocation }">
      """.trim()
			WMT_MS_Capabilities( version: "1.1.1" ) {
				Service {
					Name( 'OGC:WMS' )
					Title( 'OMAR Web Map Service' )
					Abstract( 'A compliant implementation of WMS plus most of the SLD extension (dynamic styling). Can also generate PDF, SVG, KML, GeoRSS' )
					KeywordList {
						Keyword( 'WFS' )
						Keyword( 'WMS' )
						Keyword( 'OMAR' )
					}
					OnlineResource( 'xmlns:xlink': "http://www.w3.org/1999/xlink", 'xlink:type': "simple", 'xlink:href': "http://omar.ossim.org" )
					ContactInformation {
						ContactPersonPrimary {
							ContactPerson( 'Scott Bortman' )
							ContactOrganization( 'Radiant Blue' )
						}
						ContactPosition()
						ContactAddress {
							AddressType()
							Address()
							City()
							StateOrProvince()
							PostCode()
							Country()
						}
						ContactVoiceTelephone()
						ContactFacsimileTelephone()
						ContactElectronicMailAddress( 'sbortman@radiantblue.com' )
					}
					Fees( 'NONE' )
					AccessConstraints( 'NONE' )
				}
				Capability {
					Request {
						GetCapabilities {
							Format( 'application/vnd.ogc.wms_xml' )
							DCPType {
								HTTP {
									Get {
										OnlineResource( 'xmlns:xlink': "http://www.w3.org/1999/xlink", 'xlink:type': "simple", 'xlink:href': wmsServiceAddress )
									}
//                  Post {
//                    OnlineResource( 'xmlns:xlink': "http://www.w3.org/1999/xlink",  'xlink:type': "simple", 'xlink:href': wmsServiceAddress )
//                  }
								}
							}
						}
						GetMap {
							getMapOutputFormats.each { Format( it ) }
							DCPType {
								HTTP {
									Get {
										OnlineResource( 'xmlns:xlink': "http://www.w3.org/1999/xlink", 'xlink:type': "simple", 'xlink:href': wmsServiceAddress )
									}
								}
							}
						}
					}
					VendorSpecificCapabilities {
						filter( required: '0' ) {
							Title( "Filter" )
							Abstract( "Apply a Filter to the layer before rendering it.  Can accept either CQL or OGC syntax" )
						}
					}
					Exception {
						Format( 'application/vnd.ogc.se_xml' )
						//Format( 'application/vnd.ogc.se_inimage' )
						//Format( 'application/vnd.ogc.se_blank' )
					}
					UserDefinedSymbolization( SupportSLD: "1", UserLayer: "1", UserStyle: "1", RemoteWFS: "1" )
					Layer {
						Title( 'OMAR Web Map Service' )
						Abstract( 'A compliant implementation of WMS plus most of the SLD extension (dynamic styling). Can also generate PDF, SVG, KML, GeoRSS' )
						// <!--All supported EPSG projections:-->
						supportedProjections.each {
							SRS( it )
						}
						LatLonBoundingBox( minx: "-180.0", miny: "-90.0", maxx: "180.0", maxy: "90.0" )                             \

						LayerInfo.list()?.each { layerInfo ->
							WorkspaceInfo workspaceInfo = WorkspaceInfo.findByName( layerInfo.workspaceInfo.name )

							Workspace.withWorkspace( workspaceInfo?.workspaceParams ) { Workspace workspace ->
								def layer = workspace[ layerInfo.name ]
								def bounds = layer.bounds
								def geoBounds = ( layer?.proj?.epsg == 4326 ) ? bounds : bounds?.reproject( 'epsg:4326' )

								Layer( queryable: "1" ) {
									Name( "${ layerInfo.workspaceInfo.namespaceInfo.prefix }:${ layerInfo.name }" )
									Title( layerInfo.title )
									Abstract( layerInfo.description )
									KeywordList {
										layerInfo.keywords.each { Keyword( it ) }
									}
									SRS( bounds.proj.id )
									LatLonBoundingBox( minx: geoBounds.minX, miny: geoBounds.minY, maxx: geoBounds.maxX, maxy: geoBounds.maxY )
									BoundingBox( SRS: bounds.proj.id, minx: bounds.minX, miny: bounds.minY, maxx: bounds.maxX, maxy: bounds.maxY )
								}
							}
						}
					}
				}
			}
		}

		def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

//    [contentType: 'application/vnd.ogc.wms_xml', buffer: doc]
		[ contentType: 'text/xml', buffer: xml ]

	}

	def getMap( GetMapRequest wmsParams )
	{
		log.trace "getMap: Entered ................"
		def renderMode = RenderMode.FILTER
		def otherParams = [ startDate: new Date() ]

		//println wmsParams

		def ostream = new ByteArrayOutputStream()

		switch ( renderMode )
		{
		case RenderMode.GEOSCRIPT:
			log.trace "getMap: Using  RenderMode.GEOSCRIPT Method"
			def images = wmsParams?.layers?.split( ',' )?.collect { [ imageFile: it as File ] }
			def chipperLayer = new ChipperLayer( images )

			def map = new GeoScriptMap(
					width: wmsParams?.width,
					height: wmsParams?.height,
					type: wmsParams?.format?.split( '/' )?.last(),
					proj: wmsParams?.srs,
					bounds: new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() } ), wmsParams?.srs ),
					layers: [ chipperLayer ]
			)

			map.render( ostream )
			map.close()
			break

		case RenderMode.BLANK:
			log.trace "getMap: Using  RenderMode.BLANK Method"
			def image = new BufferedImage( wmsParams.width, wmsParams.height, BufferedImage.TYPE_INT_ARGB )

			ImageIO.write( image, wmsParams?.format?.split( '/' )?.last(), ostream )
			break

		case RenderMode.FILTER:
			log.trace "getMap: Using  RenderMode.FILTER Method"

			def ( prefix, layerName ) = wmsParams?.layers?.split( ':' )

			def layerInfo = LayerInfo.where {
				name == layerName && workspaceInfo.namespaceInfo.prefix == prefix
			}.get()

			List images = null

			//def maxCount = grailsApplication?.config.omar.wms.autoMosaic.maxCount
			//println "BEFORE: ${maxCount}"
			//maxCount = maxCount?:10
			//println maxCount
			//def sorting = grailsApplication?.config.omar.wms.autoMosaic.sorting

			Workspace.withWorkspace( layerInfo.workspaceInfo.workspaceParams ) { workspace ->
				def layer = workspace[ layerName ]

				images = layer.collectFromFeature(
						filter: wmsParams?.filter,
						//sorting: sorting,
					//	max: maxCount, // will remove and change to have the wms plugin have defaults
						fields: [ 'filename', 'entry_id' ] as List<String>
				) {
					[ imageFile: it.filename as File, entry: it.entry_id?.toInteger() ]
				}
			}

			def chipperLayer = new ChipperLayer( images )

			def map = new GeoScriptMap(
					width: wmsParams?.width,
					height: wmsParams?.height,
					type: wmsParams?.format?.split( '/' )?.last(),
					proj: wmsParams?.srs,
					bounds: new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() } ), wmsParams?.srs ),
					layers: [ chipperLayer ]
			)

			map.render( ostream )
			map.close()
			break
		}

		otherParams.endDate = new Date()

		log.trace "getMap: Leaving ................"
		[ contentType: wmsParams.format, buffer: ostream.toByteArray(), metrics: otherParams ]
	}
}
