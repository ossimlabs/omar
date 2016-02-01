package omar.wms

import groovy.xml.StreamingMarkupBuilder

import geoscript.workspace.Workspace
import geoscript.geom.Bounds
import geoscript.render.Map as GeoScriptMap

import omar.geoscript.LayerInfo
import omar.geoscript.WorkspaceInfo

import javax.imageio.ImageIO
import java.awt.image.BufferedImage


class WebMappingService
{
  static transactional = false

  def grailsLinkGenerator

  enum RenderMode {
    BLANK, GEOSCRIPT, FILTER
  }

  static final def getMapOutputFormats = [
      'image/png',
      'application/atom xml',
      'application/atom+xml',
      'application/openlayers',
      'application/pdf',
      'application/rss xml',
      'application/rss+xml',
      'application/vnd.google-earth.kml',
      'application/vnd.google-earth.kml xml',
      'application/vnd.google-earth.kml+xml',
      'application/vnd.google-earth.kml+xml;mode=networklink',
      'application/vnd.google-earth.kmz',
      'application/vnd.google-earth.kmz xml',
      'application/vnd.google-earth.kmz+xml',
      'application/vnd.google-earth.kmz;mode=networklink',
      'atom',
      'image/geotiff',
      'image/geotiff8',
      'image/gif',
      'image/gif;subtype=animated',
      'image/jpeg',
      'image/png8',
      'image/png; mode=8bit',
      'image/svg',
      'image/svg xml',
      'image/svg+xml',
      'image/tiff',
      'image/tiff8',
      'kml',
      'kmz',
      'openlayers',
      'rss',
      'text/html; subtype=openlayers'
  ]

  def getCapabilities(GetCapabilitiesRequest wmsParams)
  {
    def wmsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wms' )

    def x = {
      mkp.xmlDeclaration()
      /*
<!DOCTYPE WMT_MS_Capabilities SYSTEM "http://localhost/geoserver/schemas/wms/1.1.1/WMS_MS_Capabilities.dtd">
*/
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
            /*
<GetFeatureInfo>
<Format>text/plain</Format>
<Format>application/vnd.ogc.gml</Format>
<Format>text/xml</Format>
<Format>application/vnd.ogc.gml/3.1.1</Format>
<Format>text/xml; subtype=gml/3.1.1</Format>
<Format>text/html</Format>
<Format>application/json</Format>
<DCPType>
<HTTP>
<Get>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?SERVICE=WMS&amp;" />
</Get>
<Post>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?SERVICE=WMS&amp;" />
</Post>
</HTTP>
</DCPType>
</GetFeatureInfo>

<DescribeLayer>
<Format>application/vnd.ogc.wms_xml</Format>
<DCPType>
<HTTP>
<Get>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?SERVICE=WMS&amp;" />
</Get>
</HTTP>
</DCPType>
</DescribeLayer>
<GetLegendGraphic>
<Format>image/png</Format>
<Format>image/jpeg</Format>
<Format>image/gif</Format>
<DCPType>
<HTTP>
<Get>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?SERVICE=WMS&amp;" />
</Get>
</HTTP>
</DCPType>
</GetLegendGraphic>
<GetStyles>
<Format>application/vnd.ogc.sld+xml</Format>
<DCPType>
<HTTP>
<Get>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?SERVICE=WMS&amp;" />
</Get>
</HTTP>
</DCPType>
</GetStyles>
*/
          }
          VendorSpecificCapabilities {
            filter( required: '0' ) {
              Title("Filter")
              Abstract("Apply a Filter to the layer before rendering it.  Can accept either CQL or OGC syntax")
            }
          }
          Exception {
            Format( 'application/vnd.ogc.se_xml' )
            Format( 'application/vnd.ogc.se_inimage' )
            Format( 'application/vnd.ogc.se_blank' )
          }
          UserDefinedSymbolization( SupportSLD: "1", UserLayer: "1", UserStyle: "1", RemoteWFS: "1" )
          Layer {
            Title( 'OMAR Web Map Service' )
            Abstract( 'A compliant implementation of WMS plus most of the SLD extension (dynamic styling). Can also generate PDF, SVG, KML, GeoRSS' )
            // <!--All supported EPSG projections:-->
            SRS( 'EPSG:3857' )
            SRS( 'EPSG:4326' )
            LatLonBoundingBox( minx: "-180.0", miny: "-90.0", maxx: "180.0", maxy: "90.0" )                         \

            LayerInfo.list()?.each { layerInfo ->
              WorkspaceInfo workspaceInfo = WorkspaceInfo.findByName( layerInfo.workspaceInfo.name )

              Workspace.withWorkspace( workspaceInfo?.workspaceParams ) { Workspace workspace ->
                def layer = workspace[layerInfo.name]
                def bounds = layer.bounds
                def geoBounds = ( layer?.proj?.epsg == 4326 ) ? bounds : bounds?.reproject( 'epsg:4326' )

                Layer( queryable: "1" ) {
                  Name( "${layerInfo.workspaceInfo.namespaceInfo.prefix}:${layerInfo.name}" )
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

            /*
<Layer queryable="1">
<Name>tasmania</Name>
<Title>tasmania</Title>
<Abstract>Layer-Group type layer: tasmania</Abstract>
<SRS>EPSG:4326</SRS>
<LatLonBoundingBox minx="143.83482400000003" miny="-43.648056" maxx="148.47914100000003" maxy="-39.573891" />
<BoundingBox SRS="EPSG:4326" minx="143.83482400000003" miny="-43.648056" maxx="148.47914100000003" maxy="-39.573891" />
</Layer>
<Layer queryable="1">
<Name>tiger-ny</Name>
<Title>tiger-ny</Title>
<Abstract>Layer-Group type layer: tiger-ny</Abstract>
<SRS>EPSG:4326</SRS>
<LatLonBoundingBox minx="-74.047185" miny="40.679648" maxx="-73.907005" maxy="40.882078" />
<BoundingBox SRS="EPSG:4326" minx="-74.047185" miny="40.679648" maxx="-73.907005" maxy="40.882078" />
</Layer>
<Layer queryable="1" opaque="0">
<Name>nurc:Arc_Sample</Name>
<Title>A sample ArcGrid file</Title>
<Abstract />
<KeywordList>
<Keyword>WCS</Keyword>
<Keyword>arcGridSample</Keyword>
<Keyword>arcGridSample_Coverage</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<BoundingBox SRS="EPSG:4326" minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<Style>
<Name>rain</Name>
<Title>Rain distribution</Title>
<Abstract />
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=nurc%3AArc_Sample" />
</LegendURL>
</Style>
<Style>
<Name>raster</Name>
<Title>Default Raster</Title>
<Abstract>A sample style that draws a raster, good for displaying imagery</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=nurc%3AArc_Sample&amp;style=raster" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>nurc:Img_Sample</Name>
<Title>North America sample imagery</Title>
<Abstract />
<KeywordList>
<Keyword>WCS</Keyword>
<Keyword>worldImageSample</Keyword>
<Keyword>worldImageSample_Coverage</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-130.85168" miny="20.7052" maxx="-62.0054" maxy="54.1141" />
<BoundingBox SRS="EPSG:4326" minx="-130.85168" miny="20.7052" maxx="-62.0054" maxy="54.1141" />
<Style>
<Name>raster</Name>
<Title>Default Raster</Title>
<Abstract>A sample style that draws a raster, good for displaying imagery</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=nurc%3AImg_Sample" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:archsites</Name>
<Title>Spearfish archeological sites</Title>
<Abstract>Sample data from GRASS, archeological sites location, Spearfish, South Dakota, USA</Abstract>
<KeywordList>
<Keyword>archsites</Keyword>
<Keyword>spearfish</Keyword>
<Keyword>sfArchsites</Keyword>
<Keyword>archeology</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.8725637911543" miny="44.37740330855979" maxx="-103.63794182141925" maxy="44.48804280772808" />
<BoundingBox SRS="EPSG:26713" minx="589851.4376666048" miny="4914490.882968263" maxx="608346.4603107043" maxy="4926501.8980334345" />
<Style>
<Name>point</Name>
<Title>Default Point</Title>
<Abstract>A sample style that draws a point</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Aarchsites" />
</LegendURL>
</Style>
<Style>
<Name>capitals</Name>
<Title>Capital cities</Title>
<Abstract />
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Aarchsites&amp;style=capitals" />
</LegendURL>
</Style>
<Style>
<Name>burg</Name>
<Title>A small red flag</Title>
<Abstract>A sample of how to use an SVG based symbolizer</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Aarchsites&amp;style=burg" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:bugsites</Name>
<Title>Spearfish bug locations</Title>
<Abstract>Sample data from GRASS, bug sites location, Spearfish, South Dakota, USA</Abstract>
<KeywordList>
<Keyword>spearfish</Keyword>
<Keyword>sfBugsites</Keyword>
<Keyword>insects</Keyword>
<Keyword>bugsites</Keyword>
<Keyword>tiger_beetles</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.86796131703647" miny="44.373938816704396" maxx="-103.63773523234195" maxy="44.43418821380063" />
<BoundingBox SRS="EPSG:26713" minx="590223.4382724703" miny="4914107.882513998" maxx="608462.4604629107" maxy="4920523.89081033" />
<Style>
<Name>capitals</Name>
<Title>Capital cities</Title>
<Abstract />
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Abugsites" />
</LegendURL>
</Style>
<Style>
<Name>point</Name>
<Title>Default Point</Title>
<Abstract>A sample style that draws a point</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Abugsites&amp;style=point" />
</LegendURL>
</Style>
<Style>
<Name>burg</Name>
<Title>A small red flag</Title>
<Abstract>A sample of how to use an SVG based symbolizer</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Abugsites&amp;style=burg" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>tiger:giant_polygon</Name>
<Title>World rectangle</Title>
<Abstract>A simple rectangular polygon covering most of the world, it's only used for the purpose of providing a background (WMS bgcolor could be used instead)</Abstract>
<KeywordList>
<Keyword>DS_giant_polygon</Keyword>
<Keyword>giant_polygon</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<BoundingBox SRS="EPSG:4326" minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<Style>
<Name>giant_polygon</Name>
<Title>Border-less gray fill</Title>
<Abstract>Light gray polygon fill without a border</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Agiant_polygon" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>nurc:mosaic</Name>
<Title>mosaic</Title>
<Abstract />
<KeywordList>
<Keyword>WCS</Keyword>
<Keyword>ImageMosaic</Keyword>
<Keyword>mosaic</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="6.346" miny="36.492" maxx="20.83" maxy="46.591" />
<BoundingBox SRS="EPSG:4326" minx="6.346" miny="36.492" maxx="20.83" maxy="46.591" />
<Style>
<Name>raster</Name>
<Title>Default Raster</Title>
<Abstract>A sample style that draws a raster, good for displaying imagery</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=nurc%3Amosaic" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>tiger:poi</Name>
<Title>Manhattan (NY) points of interest</Title>
<Abstract>Points of interest in New York, New York (on Manhattan). One of the attributes contains the name of a file with a picture of the point of interest.</Abstract>
<KeywordList>
<Keyword>poi</Keyword>
<Keyword>Manhattan</Keyword>
<Keyword>DS_poi</Keyword>
<Keyword>points_of_interest</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-74.0118315772888" miny="40.70754683896324" maxx="-74.00857344353275" maxy="40.711945649065406" />
<BoundingBox SRS="EPSG:4326" minx="-74.0118315772888" miny="40.70754683896324" maxx="-74.00153046439813" maxy="40.719885123828675" />
<Style>
<Name>poi</Name>
<Title>Points of interest</Title>
<Abstract>Manhattan points of interest</Abstract>
<LegendURL width="22" height="40">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoi" />
</LegendURL>
</Style>
<Style>
<Name>point</Name>
<Title>Default Point</Title>
<Abstract>A sample style that draws a point</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoi&amp;style=point" />
</LegendURL>
</Style>
<Style>
<Name>burg</Name>
<Title>A small red flag</Title>
<Abstract>A sample of how to use an SVG based symbolizer</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoi&amp;style=burg" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>tiger:poly_landmarks</Name>
<Title>Manhattan (NY) landmarks</Title>
<Abstract>Manhattan landmarks, identifies water, lakes, parks, interesting buildilngs</Abstract>
<KeywordList>
<Keyword>landmarks</Keyword>
<Keyword>DS_poly_landmarks</Keyword>
<Keyword>manhattan</Keyword>
<Keyword>poly_landmarks</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-74.047185" miny="40.679648" maxx="-73.90782" maxy="40.882078" />
<BoundingBox SRS="EPSG:4326" minx="-74.047185" miny="40.679648" maxx="-73.90782" maxy="40.882078" />
<Style>
<Name>poly_landmarks</Name>
<Title />
<Abstract />
<LegendURL width="22" height="80">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoly_landmarks" />
</LegendURL>
</Style>
<Style>
<Name>grass</Name>
<Title>Grass fill</Title>
<Abstract>A style filling polygons with a grass theme coming from a PNG file</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoly_landmarks&amp;style=grass" />
</LegendURL>
</Style>
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoly_landmarks&amp;style=polygon" />
</LegendURL>
</Style>
<Style>
<Name>restricted</Name>
<Title>Red, translucent style</Title>
<Abstract>A sample style that just prints out a transparent red interior with a red outline</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Apoly_landmarks&amp;style=restricted" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>omar:raster_entry</Name>
<Title>raster_entry</Title>
<Abstract />
<KeywordList>
<Keyword>raster_entry</Keyword>
<Keyword>features</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<BoundingBox SRS="EPSG:4326" minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=omar%3Araster_entry" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:restricted</Name>
<Title>Spearfish restricted areas</Title>
<Abstract>Sample data from GRASS, restricted areas, Spearfish, South Dakota, USA</Abstract>
<KeywordList>
<Keyword>spearfish</Keyword>
<Keyword>restricted</Keyword>
<Keyword>areas</Keyword>
<Keyword>sfRestricted</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.85057172920756" miny="44.39436387625042" maxx="-103.74741494853805" maxy="44.48215752041131" />
<BoundingBox SRS="EPSG:26713" minx="591579.1858092896" miny="4916236.662227167" maxx="599648.9251686076" maxy="4925872.146218054" />
<Style>
<Name>restricted</Name>
<Title>Red, translucent style</Title>
<Abstract>A sample style that just prints out a transparent red interior with a red outline</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Arestricted" />
</LegendURL>
</Style>
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Arestricted&amp;style=polygon" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:roads</Name>
<Title>Spearfish roads</Title>
<Abstract>Sample data from GRASS, road layout, Spearfish, South Dakota, USA</Abstract>
<KeywordList>
<Keyword>sfRoads</Keyword>
<Keyword>spearfish</Keyword>
<Keyword>roads</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.87741691493184" miny="44.37087275281798" maxx="-103.62231404880659" maxy="44.50015918338962" />
<BoundingBox SRS="EPSG:26713" minx="589434.8564686741" miny="4914006.337837095" maxx="609527.2102150217" maxy="4928063.398014731" />
<Style>
<Name>simple_roads</Name>
<Title>Default Styler for simple road segments</Title>
<Abstract>Light red line, 2px wide</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Aroads" />
</LegendURL>
</Style>
<Style>
<Name>line</Name>
<Title>Default Line</Title>
<Abstract>A sample style that draws a line</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Aroads&amp;style=line" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:sfdem</Name>
<Title>sfdem is a Tagged Image File Format with Geographic information</Title>
<Abstract />
<KeywordList>
<Keyword>WCS</Keyword>
<Keyword>sfdem</Keyword>
<Keyword>sfdem</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.87108701853181" miny="44.370187074132616" maxx="-103.62940739432703" maxy="44.5016011535299" />
<BoundingBox SRS="EPSG:26713" minx="589980.0" miny="4913700.0" maxx="609000.0" maxy="4928010.0" />
<Style>
<Name>dem</Name>
<Title>Simple DEM style</Title>
<Abstract>Classic elevation color progression</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Asfdem" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>topp:states</Name>
<Title>USA Population</Title>
<Abstract>This is some census data on the states.</Abstract>
<KeywordList>
<Keyword>census</Keyword>
<Keyword>united</Keyword>
<Keyword>boundaries</Keyword>
<Keyword>state</Keyword>
<Keyword>states</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-124.731422" miny="24.955967" maxx="-66.969849" maxy="49.371735" />
<BoundingBox SRS="EPSG:4326" minx="-124.73142200000001" miny="24.955967" maxx="-66.969849" maxy="49.371735" />
<Style>
<Name>population</Name>
<Title>Population in the United States</Title>
<Abstract>A sample filter that filters the United States into three categories of population, drawn in different colors</Abstract>
<LegendURL width="76" height="80">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Astates" />
</LegendURL>
</Style>
<Style>
<Name>pophatch</Name>
<Title>Population in the United States</Title>
<Abstract>A sample filter that filters the United States into three categories of population, drawn in different colors</Abstract>
<LegendURL width="76" height="80">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Astates&amp;style=pophatch" />
</LegendURL>
</Style>
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Astates&amp;style=polygon" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>sf:streams</Name>
<Title>Spearfish streams</Title>
<Abstract>Sample data from GRASS, streams, Spearfish, South Dakota, USA</Abstract>
<KeywordList>
<Keyword>spearfish</Keyword>
<Keyword>sfStreams</Keyword>
<Keyword>streams</Keyword>
</KeywordList>
<SRS>EPSG:26713</SRS>
<!--WKT definition of this CRS:
PROJCS["NAD27 / UTM zone 13N",
GEOGCS["NAD27",
DATUM["North American Datum 1927",
SPHEROID["Clarke 1866", 6378206.4, 294.9786982138982, AUTHORITY["EPSG","7008"]],
TOWGS84[2.478, 149.752, 197.726, 0.526, -0.498, 0.501, 0.685],
AUTHORITY["EPSG","6267"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4267"]],
PROJECTION["Transverse_Mercator", AUTHORITY["EPSG","9807"]],
PARAMETER["central_meridian", -105.0],
PARAMETER["latitude_of_origin", 0.0],
PARAMETER["scale_factor", 0.9996],
PARAMETER["false_easting", 500000.0],
PARAMETER["false_northing", 0.0],
UNIT["m", 1.0],
AXIS["Easting", EAST],
AXIS["Northing", NORTH],
AUTHORITY["EPSG","26713"]]-->
<LatLonBoundingBox minx="-103.87789019829768" miny="44.372335260095554" maxx="-103.62287788915457" maxy="44.502218486214815" />
<BoundingBox SRS="EPSG:26713" minx="589434.4971235897" miny="4913947.342298816" maxx="609518.2117427464" maxy="4928071.049965891" />
<Style>
<Name>simple_streams</Name>
<Title>Default Styler for streams segments</Title>
<Abstract>Blue lines, 2px wide</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Astreams" />
</LegendURL>
</Style>
<Style>
<Name>line</Name>
<Title>Default Line</Title>
<Abstract>A sample style that draws a line</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=sf%3Astreams&amp;style=line" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>topp:tasmania_cities</Name>
<Title>Tasmania cities</Title>
<Abstract>Cities in Tasmania (actually, just the capital)</Abstract>
<KeywordList>
<Keyword>cities</Keyword>
<Keyword>Tasmania</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="145.19754" miny="-43.423512" maxx="148.27298000000002" maxy="-40.852802" />
<BoundingBox SRS="EPSG:4326" minx="145.19754" miny="-43.423512" maxx="148.27298000000002" maxy="-40.852802" />
<Style>
<Name>capitals</Name>
<Title>Capital cities</Title>
<Abstract />
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Atasmania_cities" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>topp:tasmania_roads</Name>
<Title>Tasmania roads</Title>
<Abstract>Main Tasmania roads</Abstract>
<KeywordList>
<Keyword>Roads</Keyword>
<Keyword>Tasmania</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="145.19754" miny="-43.423512" maxx="148.27298000000002" maxy="-40.852802" />
<BoundingBox SRS="EPSG:4326" minx="145.19754" miny="-43.423512" maxx="148.27298000000002" maxy="-40.852802" />
<Style>
<Name>simple_roads</Name>
<Title>Default Styler for simple road segments</Title>
<Abstract>Light red line, 2px wide</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Atasmania_roads" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>topp:tasmania_state_boundaries</Name>
<Title>Tasmania state boundaries</Title>
<Abstract>Tasmania state boundaries</Abstract>
<KeywordList>
<Keyword>boundaries</Keyword>
<Keyword>tasmania_state_boundaries</Keyword>
<Keyword>Tasmania</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="143.83482400000003" miny="-43.648056" maxx="148.47914100000003" maxy="-39.573891" />
<BoundingBox SRS="EPSG:4326" minx="143.83482400000003" miny="-43.648056" maxx="148.47914100000003" maxy="-39.573891" />
<Style>
<Name>green</Name>
<Title>Green polygon</Title>
<Abstract>Green fill with black outline</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Atasmania_state_boundaries" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>topp:tasmania_water_bodies</Name>
<Title>Tasmania water bodies</Title>
<Abstract>Tasmania water bodies</Abstract>
<KeywordList>
<Keyword>Lakes</Keyword>
<Keyword>Bodies</Keyword>
<Keyword>Australia</Keyword>
<Keyword>Water</Keyword>
<Keyword>Tasmania</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="145.97161899999998" miny="-43.031944" maxx="147.219696" maxy="-41.775558" />
<BoundingBox SRS="EPSG:4326" minx="145.97161899999998" miny="-43.031944" maxx="147.219696" maxy="-41.775558" />
<Style>
<Name>cite_lakes</Name>
<Title>Blue lake</Title>
<Abstract>A blue fill, solid black outline style</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=topp%3Atasmania_water_bodies" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>tiger:tiger_roads</Name>
<Title>Manhattan (NY) roads</Title>
<Abstract>Highly simplified road layout of Manhattan in New York..</Abstract>
<KeywordList>
<Keyword>DS_tiger_roads</Keyword>
<Keyword>tiger_roads</Keyword>
<Keyword>roads</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-74.02722" miny="40.684221" maxx="-73.907005" maxy="40.878178" />
<BoundingBox SRS="EPSG:4326" minx="-74.02722" miny="40.684221" maxx="-73.907005" maxy="40.878178" />
<Style>
<Name>tiger_roads</Name>
<Title />
<Abstract />
<LegendURL width="22" height="80">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Atiger_roads" />
</LegendURL>
</Style>
<Style>
<Name>simple_roads</Name>
<Title>Default Styler for simple road segments</Title>
<Abstract>Light red line, 2px wide</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Atiger_roads&amp;style=simple_roads" />
</LegendURL>
</Style>
<Style>
<Name>line</Name>
<Title>Default Line</Title>
<Abstract>A sample style that draws a line</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=tiger%3Atiger_roads&amp;style=line" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>demo:usgs_tracts</Name>
<Title>Census Tracts</Title>
<Abstract />
<KeywordList>
<Keyword>features</Keyword>
<Keyword>tracts</Keyword>
</KeywordList>
<SRS>EPSG:4269</SRS>
<!--WKT definition of this CRS:
GEOGCS["NAD83",
DATUM["North American Datum 1983",
SPHEROID["GRS 1980", 6378137.0, 298.257222101, AUTHORITY["EPSG","7019"]],
TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
AUTHORITY["EPSG","6269"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4269"]]-->
<LatLonBoundingBox minx="37.828556999999996" miny="-79.03635199964832" maxx="39.723005" maxy="-75.76666699955108" />
<BoundingBox SRS="EPSG:4269" minx="37.828556999999996" miny="-79.03635200000001" maxx="39.723005" maxy="-75.766667" />
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=demo%3Ausgs_tracts" />
</LegendURL>
</Style>
</Layer>
<Layer queryable="1" opaque="0">
<Name>omar:video_data_set</Name>
<Title>video_data_set</Title>
<Abstract />
<KeywordList>
<Keyword>video_data_set</Keyword>
<Keyword>features</Keyword>
</KeywordList>
<SRS>EPSG:4326</SRS>
<!--WKT definition of this CRS:
GEOGCS["WGS 84",
DATUM["World Geodetic System 1984",
SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]],
AUTHORITY["EPSG","6326"]],
PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]],
UNIT["degree", 0.017453292519943295],
AXIS["Geodetic longitude", EAST],
AXIS["Geodetic latitude", NORTH],
AUTHORITY["EPSG","4326"]]-->
<LatLonBoundingBox minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<BoundingBox SRS="EPSG:4326" minx="-180.0" miny="-90.0" maxx="180.0" maxy="90.0" />
<Style>
<Name>polygon</Name>
<Title>Default Polygon</Title>
<Abstract>A sample style that draws a polygon</Abstract>
<LegendURL width="20" height="20">
<Format>image/png</Format>
<OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://localhost/geoserver/wms?request=GetLegendGraphic&amp;format=image%2Fpng&amp;width=20&amp;height=20&amp;layer=omar%3Avideo_data_set" />
</LegendURL>
</Style>
</Layer>
*/
          }
        }
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

//    [contentType: 'application/vnd.ogc.wms_xml', buffer: doc]
    [contentType: 'text/xml', buffer: xml]

  }

  def getMap(GetMapRequest wmsParams)
  {
    def renderMode = RenderMode.FILTER

    println wmsParams

    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.GEOSCRIPT:
      def images = wmsParams?.layers?.split( ',' )?.collect { [imageFile: it as File] }
      def chipperLayer = new ChipperLayer( images )

      def map = new GeoScriptMap(
          width: wmsParams?.width,
          height: wmsParams?.height,
          type: wmsParams?.format?.split( '/' )?.last(),
          proj: wmsParams?.srs,
          bounds: new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() } ), wmsParams?.srs ),
          layers: [chipperLayer]
      )

      map.render( ostream )
      map.close()
      break

    case RenderMode.BLANK:
      def image = new BufferedImage( wmsParams.width, wmsParams.height, BufferedImage.TYPE_INT_ARGB )

      ImageIO.write( image, wmsParams?.format?.split( '/' )?.last(), ostream )
      break

    case RenderMode.FILTER:

      def (prefix, layerName) = wmsParams?.layers?.split( ':' )

      def layerInfo = LayerInfo.where {
        name == layerName && workspaceInfo.namespaceInfo.prefix == prefix
      }.get()

      List images = null

      Workspace.withWorkspace( layerInfo.workspaceInfo.workspaceParams ) { workspace ->
        def layer = workspace[layerName]

        images = layer.collectFromFeature(
            filter: wmsParams?.filter,
            fields: ['filename', 'entry_id'] as List<String>
        ) {
          [imageFile: it.filename as File, entry: it.entry_id?.toInteger()]
        }
      }

      def chipperLayer = new ChipperLayer( images )

      def map = new GeoScriptMap(
          width: wmsParams?.width,
          height: wmsParams?.height,
          type: wmsParams?.format?.split( '/' )?.last(),
          proj: wmsParams?.srs,
          bounds: new Bounds( *( wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() } ), wmsParams?.srs ),
          layers: [chipperLayer]
      )

      map.render( ostream )
      map.close()




      break
    }

    [contentType: wmsParams.format, buffer: ostream.toByteArray()]
  }
}
