package omar.wmts

import omar.geoscript.LayerInfo
import grails.transaction.Transactional
import geoscript.workspace.Workspace
import geoscript.geom.Bounds
import geoscript.layer.Grid
import geoscript.layer.Tile
import geoscript.layer.Pyramid
import geoscript.proj.Projection
import groovy.xml.StreamingMarkupBuilder
import org.springframework.beans.factory.InitializingBean
import groovy.json.JsonSlurper
import omar.core.HttpStatus
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Transactional
class WebMapTileService implements InitializingBean{

    def grailsApplication
    def grailsLinkGenerator
    def geographicProj = new Projection("EPSG:4326")
    def minLevel = 0
    def maxLevel = 20

    def tileMatrixSets = []

    def baseUrl
    def wmtsUrl

    private static final def infoFormats = [
            'text/plain',
            'application/vnd.ogc.gml',
            'application/vnd.ogc.gml/3.1.1',
            'text/html',
            'application/json'
    ]

    private static final def formats = [
            'image/png',
            'image/jpeg'
    ]

    def getCapabilities(String baseUrl, GetCapabilitiesCommand cmd)
    {
        def layers = WmtsLayer.list().collect{ row ->
            WmtsTileMatrixSet tileMatrixSet = row.wmtsTileMatrixSet
            def bounds = tileMatrixSet.bounds
            def geoBounds = bounds
            if(bounds.proj)  geoBounds = bounds.reproject(geographicProj)
            [
                    name:    row.name,
                    title:   row.title,
                    geoMinX: geoBounds.minX,
                    geoMinY: geoBounds.minY,
                    geoMaxX: geoBounds.maxX,
                    geoMaxY: geoBounds.maxY,
                    projection: bounds.proj.id,
                    minLevel: tileMatrixSet?.minLevel,
                    maxLevel: tileMatrixSet?.maxLevel,
                    bounds: bounds,
                    tileMatrixSetName: tileMatrixSet?.name,
                    tileMatrixSet: tileMatrixSet.toTileMatrixSet()
            ]
        }

        def x = {
            mkp.xmlDeclaration()
            mkp.declareNamespace(ows: "http://www.opengis.net/ows/1.1")
            mkp.declareNamespace(xlink: "http://www.w3.org/1999/xlink")
            mkp.declareNamespace(xsi: "http://www.w3.org/2001/XMLSchema-instance")
            mkp.declareNamespace(gml: "http://www.opengis.net/gml")
            Capabilities(xmlns: "http://www.opengis.net/wmts/1.0",
                    'xsi:schemaLocation': "http://www.opengis.net/wmts/1.0 http://schemas.opengis.net/wmts/1.0/wmtsGetCapabilities_response.xsd",
                    version: "1.0.0") {
                ows.ServiceIdentification {
                    ows.Title()
                    ows.ServiceType()
                    ows.ServiceTypeVersion()
                } /* ServiceIdentification */
                ows.ServiceProvider {
                    ows.ProviderName()
                    ows.ProviderSite()
                    ows.ServiceContact {
                        ows.IndividualName()
                    } /* ServiceContact */
                } /* ServiceProvider */
                ows.OperationsMetadata {
                    ows.Operation(name: 'GetCapabilities') {
                        ows.DCP {
                            ows.HTTP {
                                ows.Get('xlink:href': "${baseUrl}/wmts") {
                                    ows.Constraint(name: "GetEncoding") {
                                        ows.AllowedValues {
                                            ows.Value('KVP')
                                        } /* AllowedValues */
                                    } /* Constraint */
                                } /* Get */
                            } /* HTTP */
                        } /* DCP */
                    } /* Operation */
                    ows.Operation(name: "GetTile") {
                        ows.DCP {
                            ows.HTTP {
                                ows.Get('xlink:href': wmtsUrl) {
                                    ows.Constraint(name: "GetEncoding") {
                                        ows.AllowedValues {
                                            ows.Value('KVP')
                                        } /* AllowedValues */
                                    } /* Constraint */
                                } /* Get */
                            } /* HTTP */
                        } /* DCP */
                    } /* Operation */
                } /* OperationsMetadata */
                Contents {
                    layers?.each { layer ->
                        Layer {
                            ows.Title(layer.title)
                            ows.WGS84BoundingBox {
                                ows.LowerCorner("${layer.geoMinX} ${layer.geoMinY}")
                                ows.UpperCorner("${layer.geoMaxX} ${layer.geoMaxY}")
                            } /* WGS84BoundingBox */
                            ows.Identifier(layer.name)
                            Style(isDefault: "true") {
                                ows.Identifier()
                            } /* Style */
                            formats.each { Format(it) }
//                            infoFormats.each { InfoFormat(it) }
                            TileMatrixSetLink {
                                TileMatrixSet(layer.tileMatrixSetName)
                                TileMatrixSetLimits {

                                    def bounds = layer.bounds

                                    bounds.proj = layer.projection

                                    def pyramid = layer.tileMatrixSet.pyramid

                                    for (def z in (layer.minLevel)..(layer.maxLevel))
                                    {
                                        def grid = pyramid.grid(z)
                                        TileMatrixLimits {
                                            TileMatrix("${z}")
                                            MinTileRow(0)
                                            MaxTileRow(grid.height - 1)
                                            MinTileCol(0)
                                            MaxTileCol(grid.width - 1)
                                        } /* TileMatrixLimits */
                                    } /* minLevel..maxLevel */
                                } /* TileMatrixLimits */
                            } /* TileMatrixSetLink */

                        }
                    }

                    WmtsTileMatrixSet.list().each { wmtsTileMatrixSet ->
                        def tileMatrixSet = wmtsTileMatrixSet.toTileMatrixSet()
                        TileMatrixSet {
                            ows.Identifier(tileMatrixSet.identifier)
                            tileMatrixSet.tileMatrices.each { tileMatrix ->
                                TileMatrix {
                                    ows.Identifier(tileMatrix.identifier)
                                    ScaleDenominator(tileMatrix.scaleDenominator)
                                    TopLeftCorner("${tileMatrix.topLeftCorner.x} ${tileMatrix.topLeftCorner.y}")
                                    TileWidth(tileMatrix.tileWidth)
                                    TileHeight(tileMatrix.tileHeight)
                                    MatrixWidth(tileMatrix.matrixWidth)
                                    MatrixHeight(tileMatrix.matrixHeight)
                                } /* TileMatrix */
                            } /* tileMatrices.each */
                        } /* TileMatrixSet */
                    } /* tileMatrixSets.each */
                }
            }
        }
        def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

//    [contentType: 'application/vnd.ogc.wms_xml', buffer: doc]
        [ contentType: 'text/xml', buffer: xml ]
    }

    def createBufferedImage(GetTileCommand cmd, int tileWidth, int tileHeight)
    {
        def imageType = cmd?.format?.split( '/' )?.last()
        def image

        switch(imageType.toUpperCase())
        {
            case "JPEG":
                image = new BufferedImage( tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB )
                break
            default:
                image = new BufferedImage( tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB )
                break
        }

        image
    }

    def createBlankImageOutput(GetTileCommand cmd)
    {
        def result = [status:200,
                      data: null,
                      contentType: null]
        def imageType = cmd?.format?.split( '/' )?.last()
        def outputStream = new ByteArrayOutputStream()

        def image = createBufferedImage(cmd, 256, 256)

        ImageIO.write( image, cmd?.format?.split( '/' )?.last(), outputStream )

        result.data        = outputStream.toByteArray()
        result.contentType = cmd.format

        result
    }
    def getTile(GetTileCommand cmd)
    {
//        println cmd
        def result = [status:400,
                      data: null,
                      contentType: null]
        def wmtsLayer = WmtsLayer.findByName(cmd.layer)
        int tileWidth  = 256
        int tileHeight = 256
        int level = cmd.tileMatrix
        int row = cmd.tileRow
        int col = cmd.tileCol

        if(wmtsLayer&&(cmd.tileMatrixSet?.toLowerCase() == wmtsLayer.wmtsTileMatrixSet.name.toLowerCase()))
        {
            TileMatrixSet tileMatrixSet = wmtsLayer.wmtsTileMatrixSet.toTileMatrixSet()
            Pyramid pyramid = tileMatrixSet.pyramid
            tileWidth = tileMatrixSet.tileWidth
            tileHeight = tileMatrixSet.tileHeight

            Tile tile = new Tile(level, col, row)

            Bounds tileBounds = pyramid.bounds(tile)
            def b = tileBounds.reproject(geographicProj)
            String intersects = "INTERSECTS(ground_geom, POLYGON((${b.minX} ${b.minY},${b.minX} ${b.maxY},${b.maxX} ${b.maxY},${b.maxX} ${b.minY},${b.minX} ${b.minY})))"
            String filter = intersects

            if(wmtsLayer.filter)
            {
                filter = "(${wmtsLayer.filter}) AND (${filter})"
            }

            HashMap urlWfsParams = [
                    SERVICE:"WFS",
                    VERSION:"1.1.0",
                    REQUEST:"GetFeature",
                    typeName: "omar:raster_entry",
                    resultType: "results",
                    outputFormat:"JSON",
                    propertyName:"id,filename",
                    maxFeatures:10,
                    filter:filter,
                    sortBy:wmtsLayer?.sortBy?:""
            ]

            HashMap urlWmsParams = [
                    SERVICE:"WMS",
                    VERSION:"1.1.1",
                    REQUEST:"GetMap",
                    layers: "omar:raster_entry",
                    format: cmd.format,
                    filter:"",
                    width: tileWidth,
                    height:tileHeight,
                    bbox:"${b.minX},${b.minY},${b.maxX},${b.maxY}",
                    srs: b.proj.id
            ]
            URL wfsUrl = new URL("${OmarWmtsUtils.wmtsConfig.wfsUrl}")

            urlWfsParams = urlWfsParams+wfsUrl.params

            urlWfsParams.each{k,v->urlWfsParams."${k}" = v?.encodeAsURL()}
            wfsUrl.setParams(urlWfsParams)
            String wfsUrlText
            try{
                wfsUrlText=wfsUrl.text
                // println wfsUrlText
            }
            catch(e)
            {
                println e.message
                log.debug(e.toString())
                //  println e
                wfsUrlText=""
            }

            def obj
            try{
                if(wfsUrlText) obj = new JsonSlurper().parseText( wfsUrlText )
            }
            catch(e)
            {
                println e.message
                log.debug(e.toString())
                //  println e
                obj = null
            }

            def filenames = obj?.features?.collect{
                it.properties.filename
            }

            def ids = obj?.features?.collect{
                it.properties.id
            }
            if(ids)
            {
                if(OmarWmtsUtils.wmtsConfig.legacyWmsFlag)
                {
                   urlWmsParams.layers = "${ids.join(',')}"
                }
                else
                {
                   urlWmsParams.filter = "id in (${ids.join(',')})"

                }
               // urlWmsParams.filter = "id in (${ids.join(',')})"
                URL wmsUrl = new URL("${OmarWmtsUtils.wmtsConfig.wmsUrl}")
                urlWmsParams = urlWmsParams+wmsUrl.params
                urlWmsParams.each{k,v->urlWmsParams."${k}" = v?.encodeAsURL()}
                wmsUrl.setParams(urlWmsParams)
                try{
                    HttpURLConnection connection = (HttpURLConnection)wmsUrl.openConnection();
                    Map responseMap = connection.headerFields;
                    String contentType =  responseMap."Content-Type"[0].split(";")[0]

                    def outputStream = new ByteArrayOutputStream()

                    outputStream << connection.inputStream

                    result.data = outputStream.toByteArray()
                    result.contentType = contentType
                    result.status = connection.responseCode
                }
                catch(e)
                {
                    println e.message
                    log.debug(e.toString())
                    result.status = 400
                }
            }
        }

        if(result.status != 200)
        {
            //println 'BLANK!!!!'
            log.debug("WMTS: Returning blank Image")
            result = createBlankImageOutput(cmd)
            result.status = 404
        }
        result
    }
    def getLayers(GetLayersCommand cmd){
        HashMap result = [
                statusCode:HttpStatus.OK,
                status:HttpStatus.SUCCESS,
                data:[],
                pagination: [
                        count: 0,
                        offset: 0,
                        limit: 0
                ]
        ]

        result.pagination.count = WmtsLayer.count()
        result.pagination.offset = 0
        result.pagination.limit = result.pagination.count

        WmtsLayer.list().each{layer->
            result.data <<
                    [
                            name:layer.name,
                            title:layer.title,
                            description: layer.description,
                            filter: layer.filter,
                            sortBy: layer.sortBy,
                            tileMatrixName: layer.wmtsTileMatrixSet?.name,
                            minX:layer.wmtsTileMatrixSet?.minX,
                            minY:layer.wmtsTileMatrixSet?.minY,
                            maxX:layer.wmtsTileMatrixSet?.maxX,
                            maxY:layer.wmtsTileMatrixSet?.maxY,
                            minLevel: layer.wmtsTileMatrixSet?.minLevel,
                            maxLevel: layer.wmtsTileMatrixSet?.maxLevel,
                            tileWidth: layer.wmtsTileMatrixSet?.tileWidth,
                            tileHeight: layer.wmtsTileMatrixSet?.tileHeight,
                            epsgCode: layer.wmtsTileMatrixSet?.epsgCode
                    ]
        }

        result
    }
    @Override
    void afterPropertiesSet() throws Exception
    {

    }
}
