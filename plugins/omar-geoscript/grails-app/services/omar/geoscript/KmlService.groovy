package omar.geoscript


import geoscript.geom.GeometryCollection
import grails.transaction.Transactional
import groovy.xml.StreamingMarkupBuilder


@Transactional//( readOnly = true )
class KmlService { //implements InitializingBean {
    def grailsApplication


    def getFeaturesKml(features, params) {
        def kmlNode = {
            mkp.xmlDeclaration()
            kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
                Document() {

                    mkp.yieldUnescaped( getKmlStyles() )

                    def wmsParams = getKmlWmsParams(params)
                    Folder() {
                        name( "Images" )
                        features.eachWithIndex() { value, index ->
                            def feature = value
                            mkp.yieldUnescaped( getKmlGroundOverlay(index, feature, wmsParams) )
                        }
                        open( 1 )
                    }

                    if (params.footprints != "off") {
                        Folder() {
                            name( "Footprints" )
                            features.eachWithIndex() { value, index ->
                                def feature = value
                                mkp.yieldUnescaped( getKmlFootprint(index, feature) )
                            }
                            open( 1 )
                        }
                        open( 1 )
                    }
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

        def bounds = feature.ground_geom.envelopeInternal
        def centerLon = ( bounds?.minX + bounds?.maxX ) * 0.5
        def centerLat = ( bounds?.minY + bounds?.maxY ) * 0.5
        def location = "${centerLat},${centerLon}"
        def filter = "in(${feature.get("id")})"
        def tlvUrl = "${grailsApplication.config.omar.tlv.baseUrl}?" +
            "location=${location}&" +
            "filter=${filter}"

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
            "Filename": feature.filename,
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
            "View:": "<a href = '${tlvUrl}'>Ortho</a>",
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

    def getKmlFootprint(index, feature) {
        def kmlNode = {
            Placemark() {
                description { mkp.yieldUnescaped( "<![CDATA[${getKmlDescription(feature)}]]>" ) }
                name( "${index + 1}: " + (feature.title ?: new File(feature.filename).name) )

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

        def kmlWriter = new StringWriter()
        def kmlBuilder = new StreamingMarkupBuilder()
        kmlWriter << kmlBuilder.bind( kmlNode )


        return kmlWriter.buffer
    }

    def getKmlGroundOverlay(index, feature, wmsParams) {
        def kmlNode = {
            GroundOverlay() {
                description { mkp.yieldUnescaped( "<![CDATA[${getKmlDescription(feature)}]]>" ) }
                name( "${index + 1}: " + (feature.title ?: new File(feature.filename).name) )

                Icon() {
                    def wmsUrl = grailsApplication.config.omar.wms.baseUrl + "/wms?"
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

        def kmlWriter = new StringWriter()
        def kmlBuilder = new StreamingMarkupBuilder()
        kmlWriter << kmlBuilder.bind( kmlNode )


        return kmlWriter.buffer
    }

    def getKmlStyles() {
        def kmlNode = {
            Style( id: "default" ) {
                LineStyle() {
                    color( "ffffffff" )
                    width( 2 )
                }
                PolyStyle() {
                    color( "ffffffff" )
                    fill( 0 )
                }
            }
            Style( id: "msi" ) {
                LineStyle() {
                    color( "ff0000ff" )
                    width( 2 )
                }
                PolyStyle() {
                    color( "ff0000ff" )
                    fill( 0 )
                }
            }
            Style( id: "vis" ) {
                LineStyle() {
                    color( "ff00ffff" )
                    width( 2 )
                }
                PolyStyle() {
                    color( "ff00ffff" )
                    fill( 0 )
                }
            }
        }

        def kmlWriter = new StringWriter()
        def kmlBuilder = new StreamingMarkupBuilder()
        kmlWriter << kmlBuilder.bind( kmlNode )


        return kmlWriter.buffer
    }

    def getKmlWmsParams(params) {
        return [
            BANDS: "default",
            FORMAT: "image/png",
            LAYERS: "omar:raster_entry",
            REQUEST: "GetMap",
            SERVICE: "WMS",
            SRS: "EPSG:4326",
            TRANSPARENT: true,
            VERSION: "1.1.1"
        ]
    }

/*
  @Override
  void afterPropertiesSet() throws Exception
  {
    Function.registerFunction( "queryCollection" ) { String layerName, String attributeName, String filter ->
      def (workspace, layer) = getWorkspaceAndLayer( layerName )
      def results = layer?.collectFromFeature( filter ) { it[attributeName] }
      workspace?.close()
      results
    }

    Function.registerFunction( 'collectGeometries' ) { def geometries ->
      def multiType = ( geometries ) ? "geoscript.geom.Multi${geometries[0].class.simpleName}" : new GeometryCollection( geometries )

      Class.forName( multiType ).newInstance( geometries )
    }
  }*/
}
