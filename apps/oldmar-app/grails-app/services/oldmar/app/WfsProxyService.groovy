package oldmar.app

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import groovy.json.JsonSlurper
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

@Transactional( readOnly = true )
class WfsProxyService
{
  @Value( '${oldmar.wfs.endpoint}' )
  String wfsEndpoint

  def grailsLinkGenerator

  static final def featureTypes = [[
      name: 'omar:raster_entry',
      title: 'raster_entry',
      description: '',
      keywords: ['raster_entry', 'features'],
      proj: 'EPSG:4326',
      geoBbox: [maxx: "180.0", maxy: "90.0", minx: "-180.0", miny: "-90.0"],
      fields: [
          [maxOccurs: "1", minOccurs: "1", name: "id", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "1", name: "version", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "access_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "acquisition_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "azimuth_angle", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "0", name: "be_number", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "bit_depth", nillable: "false", type: "xsd:int"],
          [maxOccurs: "1", minOccurs: "0", name: "class_name", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "cloud_cover", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "0", name: "country_code", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "data_type", nillable: "false", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "description", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "entry_id", nillable: "false", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "exclude_policy", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "file_type", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "filename", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "grazing_angle", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "1", name: "ground_geom", nillable: "false", type: "gml:MultiPolygonPropertyType"],
          [maxOccurs: "1", minOccurs: "0", name: "gsd_unit", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "gsdx", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "0", name: "gsdy", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "1", name: "height", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "image_category", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "image_id", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "image_representation", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "index_id", nillable: "false", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "ingest_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "isorce", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "keep_forever", nillable: "true", type: "xsd:boolean"],
          [maxOccurs: "1", minOccurs: "0", name: "mission_id", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "niirs", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "1", name: "number_of_bands", nillable: "false", type: "xsd:int"],
          [maxOccurs: "1", minOccurs: "0", name: "number_of_res_levels", nillable: "true", type: "xsd:int"],
          [maxOccurs: "1", minOccurs: "0", name: "organization", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "other_tags_xml", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "product_id", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "raster_data_set_id", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "receive_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "release_id", nillable: "true", type: "xsd:decimal"],
          [maxOccurs: "1", minOccurs: "0", name: "security_classification", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "security_code", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "sensor_id", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "style_id", nillable: "true", type: "xsd:decimal"],
          [maxOccurs: "1", minOccurs: "0", name: "sun_azimuth", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "0", name: "sun_elevation", nillable: "true", type: "xsd:double"],
          [maxOccurs: "1", minOccurs: "0", name: "target_id", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "tie_point_set", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "title", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "valid_model", nillable: "true", type: "xsd:int"],
          [maxOccurs: "1", minOccurs: "0", name: "wac_code", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "1", name: "width", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "crosses_dateline", nillable: "true", type: "xsd:boolean"]
      ]
  ], [
      name: 'omar:video_data_set',
      title: 'video_data_set',
      description: '',
      keywords: ['video_data_set', 'features'],
      proj: 'EPSG:4326',
      geoBbox: [maxx: "180.0", maxy: "90.0", minx: "-180.0", miny: "-90.0"],
      fields: [
          [maxOccurs: "1", minOccurs: "1", name: "id", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "1", name: "version", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "end_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "filename", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "ground_geom", nillable: "true", type: "gml:MultiPolygonPropertyType"],
          [maxOccurs: "1", minOccurs: "1", name: "height", nillable: "false", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "1", name: "index_id", nillable: "false", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "other_tags_xml", nillable: "true", type: "xsd:string"],
          [maxOccurs: "1", minOccurs: "0", name: "repository_id", nillable: "true", type: "xsd:long"],
          [maxOccurs: "1", minOccurs: "0", name: "start_date", nillable: "true", type: "xsd:dateTime"],
          [maxOccurs: "1", minOccurs: "0", name: "style_id", nillable: "true", type: "xsd:decimal"],
          [maxOccurs: "1", minOccurs: "1", name: "width", nillable: "false", type: "xsd:long"]
      ]
  ]]

  static final def getFeatureOutputFormats = [
      'CSV',
      'GEOJSON',
      'GML2',
      'JSON',
      'KML',
      'KMLQUERY',
      'SHAPE-ZIP'
  ]

  static final def spatialOperators = [
      'Disjoint',
      'Equals',
      'DWithin',
      'Beyond',
      'Intersect',
      'Touches',
      'Crosses',
      'Within',
      'Contains',
      'Overlaps',
      'BBOX'
  ]

  static final def comparisonOperators = [
      'Simple_Comparisons',
      'Between',
      'Like',
      'NullCheck'
  ]

  static final def functionList = [

      [nArgs: "1", name: "abs"],
      [nArgs: "1", name: "abs_2"],
      [nArgs: "1", name: "abs_3"],
      [nArgs: "1", name: "abs_4"],
      [nArgs: "1", name: "acos"],
      [nArgs: "2", name: "AddCoverages"],
      [nArgs: "-1", name: "Affine"],
      [nArgs: "-2", name: "Aggregate"],
      [nArgs: "1", name: "Area"],
      [nArgs: "1", name: "area"],
      [nArgs: "1", name: "area2"],
      [nArgs: "3", name: "AreaGrid"],
      [nArgs: "1", name: "asin"],
      [nArgs: "1", name: "atan"],
      [nArgs: "2", name: "atan2"],
      [nArgs: "-1", name: "BandMerge"],
      [nArgs: "-2", name: "BandSelect"],
      [nArgs: "-6", name: "BarnesSurface"],
      [nArgs: "3", name: "between"],
      [nArgs: "1", name: "boundary"],
      [nArgs: "1", name: "boundaryDimension"],
      [nArgs: "1", name: "Bounds"],
      [nArgs: "2", name: "buffer"],
      [nArgs: "-2", name: "BufferFeatureCollection"],
      [nArgs: "3", name: "bufferWithSegments"],
      [nArgs: "7", name: "Categorize"],
      [nArgs: "1", name: "ceil"],
      [nArgs: "1", name: "centroid"],
      [nArgs: "1", name: "Centroid"],
      [nArgs: "2", name: "classify"],
      [nArgs: "-2", name: "Clip"],
      [nArgs: "1", name: "CollectGeometries"],
      [nArgs: "1", name: "Collection_Average"],
      [nArgs: "1", name: "Collection_Bounds"],
      [nArgs: "0", name: "Collection_Count"],
      [nArgs: "1", name: "Collection_Max"],
      [nArgs: "1", name: "Collection_Median"],
      [nArgs: "1", name: "Collection_Min"],
      [nArgs: "1", name: "Collection_Nearest"],
      [nArgs: "1", name: "Collection_Sum"],
      [nArgs: "1", name: "Collection_Unique"],
      [nArgs: "-2", name: "Concatenate"],
      [nArgs: "2", name: "contains"],
      [nArgs: "-1", name: "Contour"],
      [nArgs: "2", name: "convert"],
      [nArgs: "1", name: "convexHull"],
      [nArgs: "1", name: "cos"],
      [nArgs: "1", name: "Count"],
      [nArgs: "2", name: "CropCoverage"],
      [nArgs: "2", name: "crosses"],
      [nArgs: "2", name: "dateFormat"],
      [nArgs: "2", name: "dateParse"],
      [nArgs: "2", name: "densify"],
      [nArgs: "2", name: "difference"],
      [nArgs: "1", name: "dimension"],
      [nArgs: "2", name: "disjoint"],
      [nArgs: "2", name: "disjoint3D"],
      [nArgs: "2", name: "distance"],
      [nArgs: "2", name: "distance3D"],
      [nArgs: "1", name: "double2bool"],
      [nArgs: "1", name: "endAngle"],
      [nArgs: "1", name: "endPoint"],
      [nArgs: "1", name: "env"],
      [nArgs: "1", name: "envelope"],
      [nArgs: "2", name: "EqualInterval"],
      [nArgs: "2", name: "equalsExact"],
      [nArgs: "3", name: "equalsExactTolerance"],
      [nArgs: "2", name: "equalTo"],
      [nArgs: "1", name: "exp"],
      [nArgs: "1", name: "exteriorRing"],
      [nArgs: "3", name: "Feature"],
      [nArgs: "1", name: "floor"],
      [nArgs: "1", name: "geometryType"],
      [nArgs: "1", name: "geomFromWKT"],
      [nArgs: "1", name: "geomLength"],
      [nArgs: "2", name: "getGeometryN"],
      [nArgs: "1", name: "getX"],
      [nArgs: "1", name: "getY"],
      [nArgs: "1", name: "getz"],
      [nArgs: "2", name: "greaterEqualThan"],
      [nArgs: "2", name: "greaterThan"],
      [nArgs: "-3", name: "Grid"],
      [nArgs: "-5", name: "Heatmap"],
      [nArgs: "0", name: "id"],
      [nArgs: "2", name: "IEEEremainder"],
      [nArgs: "3", name: "if_then_else"],
      [nArgs: "11", name: "in10"],
      [nArgs: "3", name: "in2"],
      [nArgs: "4", name: "in3"],
      [nArgs: "5", name: "in4"],
      [nArgs: "6", name: "in5"],
      [nArgs: "7", name: "in6"],
      [nArgs: "8", name: "in7"],
      [nArgs: "9", name: "in8"],
      [nArgs: "10", name: "in9"],
      [nArgs: "2", name: "InclusionFeatureCollection"],
      [nArgs: "1", name: "int2bbool"],
      [nArgs: "1", name: "int2ddouble"],
      [nArgs: "1", name: "interiorPoint"],
      [nArgs: "2", name: "interiorRingN"],
      [nArgs: "-5", name: "Interpolate"],
      [nArgs: "2", name: "intersection"],
      [nArgs: "-2", name: "IntersectionFeatureCollection"],
      [nArgs: "2", name: "intersects"],
      [nArgs: "2", name: "intersects3D"],
      [nArgs: "1", name: "isClosed"],
      [nArgs: "0", name: "isCoverage"],
      [nArgs: "1", name: "isEmpty"],
      [nArgs: "2", name: "isLike"],
      [nArgs: "1", name: "isNull"],
      [nArgs: "2", name: "isometric"],
      [nArgs: "1", name: "isRing"],
      [nArgs: "1", name: "isSimple"],
      [nArgs: "1", name: "isValid"],
      [nArgs: "3", name: "isWithinDistance"],
      [nArgs: "3", name: "isWithinDistance3D"],
      [nArgs: "2", name: "Jenks"],
      [nArgs: "1", name: "length"],
      [nArgs: "2", name: "lessEqualThan"],
      [nArgs: "2", name: "lessThan"],
      [nArgs: "-1", name: "list"],
      [nArgs: "1", name: "log"],
      [nArgs: "4", name: "LRSGeocode"],
      [nArgs: "-4", name: "LRSMeasure"],
      [nArgs: "5", name: "LRSSegment"],
      [nArgs: "2", name: "max"],
      [nArgs: "2", name: "max_2"],
      [nArgs: "2", name: "max_3"],
      [nArgs: "2", name: "max_4"],
      [nArgs: "2", name: "min"],
      [nArgs: "2", name: "min_2"],
      [nArgs: "2", name: "min_3"],
      [nArgs: "2", name: "min_4"],
      [nArgs: "1", name: "mincircle"],
      [nArgs: "1", name: "minimumdiameter"],
      [nArgs: "1", name: "minrectangle"],
      [nArgs: "2", name: "modulo"],
      [nArgs: "2", name: "MultiplyCoverages"],
      [nArgs: "-2", name: "Nearest"],
      [nArgs: "1", name: "not"],
      [nArgs: "2", name: "notEqualTo"],
      [nArgs: "2", name: "numberFormat"],
      [nArgs: "5", name: "numberFormat2"],
      [nArgs: "1", name: "numGeometries"],
      [nArgs: "1", name: "numInteriorRing"],
      [nArgs: "1", name: "numPoints"],
      [nArgs: "1", name: "octagonalenvelope"],
      [nArgs: "3", name: "offset"],
      [nArgs: "2", name: "overlaps"],
      [nArgs: "-1", name: "parameter"],
      [nArgs: "1", name: "parseBoolean"],
      [nArgs: "1", name: "parseDouble"],
      [nArgs: "1", name: "parseInt"],
      [nArgs: "1", name: "parseLong"],
      [nArgs: "0", name: "pi"],
      [nArgs: "-1", name: "PointBuffers"],
      [nArgs: "2", name: "pointN"],
      [nArgs: "-6", name: "PointStacker"],
      [nArgs: "-1", name: "PolygonExtraction"],
      [nArgs: "1", name: "polygonize"],
      [nArgs: "2", name: "pow"],
      [nArgs: "1", name: "property"],
      [nArgs: "1", name: "PropertyExists"],
      [nArgs: "2", name: "Quantile"],
      [nArgs: "-1", name: "Query"],
      [nArgs: "0", name: "random"],
      [nArgs: "-1", name: "RangeLookup"],
      [nArgs: "-1", name: "RasterAsPointCollection"],
      [nArgs: "-2", name: "RasterZonalStatistics"],
      [nArgs: "5", name: "Recode"],
      [nArgs: "-2", name: "RectangularClip"],
      [nArgs: "2", name: "relate"],
      [nArgs: "3", name: "relatePattern"],
      [nArgs: "-1", name: "reproject"],
      [nArgs: "-1", name: "Reproject"],
      [nArgs: "-3", name: "rescaleToPixels"],
      [nArgs: "1", name: "rint"],
      [nArgs: "1", name: "round"],
      [nArgs: "1", name: "round_2"],
      [nArgs: "1", name: "roundDouble"],
      [nArgs: "-5", name: "ScaleCoverage"],
      [nArgs: "2", name: "setCRS"],
      [nArgs: "2", name: "simplify"],
      [nArgs: "3", name: "Simplify"],
      [nArgs: "1", name: "sin"],
      [nArgs: "-2", name: "Snap"],
      [nArgs: "2", name: "splitPolygon"],
      [nArgs: "1", name: "sqrt"],
      [nArgs: "2", name: "StandardDeviation"],
      [nArgs: "1", name: "startAngle"],
      [nArgs: "1", name: "startPoint"],
      [nArgs: "1", name: "strCapitalize"],
      [nArgs: "2", name: "strConcat"],
      [nArgs: "2", name: "strEndsWith"],
      [nArgs: "2", name: "strEqualsIgnoreCase"],
      [nArgs: "2", name: "strIndexOf"],
      [nArgs: "4", name: "stringTemplate"],
      [nArgs: "2", name: "strLastIndexOf"],
      [nArgs: "1", name: "strLength"],
      [nArgs: "2", name: "strMatches"],
      [nArgs: "3", name: "strPosition"],
      [nArgs: "4", name: "strReplace"],
      [nArgs: "2", name: "strStartsWith"],
      [nArgs: "3", name: "strSubstring"],
      [nArgs: "2", name: "strSubstringStart"],
      [nArgs: "1", name: "strToLowerCase"],
      [nArgs: "1", name: "strToUpperCase"],
      [nArgs: "1", name: "strTrim"],
      [nArgs: "3", name: "strTrim2"],
      [nArgs: "2", name: "StyleCoverage"],
      [nArgs: "2", name: "symDifference"],
      [nArgs: "1", name: "tan"],
      [nArgs: "1", name: "toDegrees"],
      [nArgs: "1", name: "toRadians"],
      [nArgs: "2", name: "touches"],
      [nArgs: "1", name: "toWKT"],
      [nArgs: "2", name: "Transform"],
      [nArgs: "2", name: "union"],
      [nArgs: "2", name: "UnionFeatureCollection"],
      [nArgs: "2", name: "Unique"],
      [nArgs: "2", name: "UniqueInterval"],
      [nArgs: "-4", name: "VectorToRaster"],
      [nArgs: "3", name: "VectorZonalStatistics"],
      [nArgs: "1", name: "vertices"],
      [nArgs: "2", name: "within"]
  ]

  def handleRequestGET(def params)
  {
//    println "GET: ${params}"

    def op = params.find { it.key.toUpperCase() == 'REQUEST' }.value

//    println "${op} ${params}"

    def contentType = 'text/xml'
    def results

    switch ( op.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      def doc = getCapabilities()

      results = [contentType: contentType, text: doc]
      break
    case 'DESCRIBEFEATURETYPE':
      def typeName = params.find { it.key.toUpperCase() == 'TYPENAME' }?.value
      def doc = describeFeatureType( typeName )
      results = [contentType: 'text/xml', text: doc]
      break
    case 'GETFEATURE':
      def typeName = params.find { it.key.toUpperCase() == 'TYPENAME' }?.value
      def filter = params.find { it.key.toUpperCase() == 'FILTER' }?.value
      def outputFormat = params.find { it.key.toUpperCase() == 'OUTPUTFORMAT' }?.value
      def doc = getFeature( typeName, filter, outputFormat )
      results = [contentType: 'text/xml', text: doc]
      break
    }

    results
  }

  def handleRequestPOST(def xml)
  {
//    println "POST:"

    def op = xml.name()

//    println "${op} ${XmlUtil.serialize( xml )}"


    def results

    switch ( op?.toUpperCase() )
    {
    case 'GETCAPABILITIES':
      def doc = getCapabilities()

      results = [contentType: contentType, text: doc]
      break
    case 'DESCRIBEFEATURETYPE':
      def typeName = xml.TypeName.text() as String
      def doc = describeFeatureType( typeName )
      results = [contentType: 'text/xml', text: doc]
      break
    case 'GETFEATURE':
      def typeName = xml.Query.@typeName.text() as String

      def filter = ( xml?.Query?.Filter ) ? xml?.Query?.collect {
        new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim()
      }?.first() : null

      def maxFeatures = xml.@maxFeatures?.text()
      def resultType = xml.@resultType?.text()
      def outputFormat = null
      def doc = getFeature( typeName, filter, outputFormat, maxFeatures, resultType )

      results = [contentType: 'text/xml', text: doc]

      break
    }

    results
  }

  def getCapabilities()
  {
    def wfsUrl = grailsLinkGenerator.link( absolute: true, uri: '/wfs' )
    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          ogc: "http://www.opengis.net/ogc",
          omar: "http://omar.ossim.org",
          xsi: "http://www.w3.org/2001/XMLSchema-instance"
      )
      WFS_Capabilities( version: "1.0.0", xmlns: "http://www.opengis.net/wfs",
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-capabilities.xsd" ) {

        Service {
          Name( 'OMAR WFS' )
          Title( 'OMAR Web Feature Service' )
          Abstract( 'This is the WFS implementation for OMAR' )
          Keywords( 'WFS, OMAR' )
          OnlineResource( wfsUrl ) // changeme
          Fees( 'NONE' )
          AccessConstraints( 'NONE' )
        }
        Capability {
          Request {
            GetCapabilities {
              DCPType {
                HTTP {
                  Get( onlineResource: "${wfsUrl}?request=GetCapabilities" ) // changeme
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: wfsUrl ) // changeme
                }
              }
            }
            DescribeFeatureType {
              SchemaDescriptionLanguage { XMLSCHEMA() }
              DCPType {
                HTTP {
                  Get( onlineResource: "${wfsUrl}?request=DescribeFeatureType" ) // chnageme
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: wfsUrl ) // changeme
                }
              }
            }
            GetFeature {
              ResultFormat {
                getFeatureOutputFormats.each { "${it}"() }
              }
              DCPType {
                HTTP {
                  Get( onlineResource: "${wfsUrl}?request=GetFeature" ) // changeme
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: wfsUrl ) // changeme
                }
              }
            }
          }
        }
        FeatureTypeList {
          Operations {
            Query()
          }
          featureTypes.each { featureType ->
            FeatureType {
              Name( featureType.name )
              Title( featureType.title )
              Abstract( featureType.description )
              Keywords( featureType?.keywords?.join( ', ' ) )
              SRS( featureType.proj )
              LatLongBoundingBox( featureType.geoBbox )
            }
          }
        }
        ogc.Filter_Capabilities {
          ogc.Spatial_Capabilities {
            ogc.Spatial_Operators {
              spatialOperators.each {
                ogc."${it}"()
              }
            }
          }
          ogc.Scalar_Capabilities {
            ogc.Logical_Operators()
            ogc.Comparison_Operators {
              comparisonOperators.each { ogc."${it}"() }
            }
            ogc.Arithmetic_Operators {
              ogc.Simple_Arithmetic()

              ogc.Functions {
                ogc.Function_Names {
                  functionList.each {
                    ogc.Function_Name( nArgs: it.nArgs, it.name )
                  }
                }
              }
            }
          }
        }
      }
    }

    new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x ).toString()
  }

  def describeFeatureType(def typeName)
  {
    def featureType = featureTypes.find { it.name == typeName }

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          omar: "http://omar.ossim.org",
          xsd: "http://www.w3.org/2001/XMLSchema",
          gml: "http://www.opengis.net/gml"
      )
      xsd.schema( elementFormDefault: "qualified", targetNamespace: "http://omar.ossim.org" ) {
        xsd.import( namespace: "http://www.opengis.net/gml", schemaLocation: "http://schemas.opengis.net/gml/2.1.2/feature.xsd" )
        xsd.complexType( name: "${featureType.title}Type" ) {
          xsd.complexContent {
            xsd.extension( base: "gml:AbstractFeatureType" ) {
              xsd.sequence {
                featureType?.fields?.each { field ->
                  xsd.element( field )
                }
              }
            }
          }
        }
        xsd.element( name: "${featureType.title}", substitutionGroup: "gml:_Feature", type: "${featureType.name}Type" )
      }
    }

    new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x ).toString()
  }

  def getFeature(def typeName, def filter, def outputFormat, def maxFeatures = 10, def resultType = 'results')
  {

    def featureJSON = fetchJSON( typeName, filter, maxFeatures, resultType )
    
    def (prefix, layerName) = typeName?.split( ':' )

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
          wfs: "http://www.opengis.net/wfs",
          gml: "http://www.opengis.net/gml",
          omar: "http://omar.ossim.org",
          xsi: "http://www.w3.org/2001/XMLSchema-instance",
      )

      def featureSchemaURL = grailsLinkGenerator.link( absolute: true, uri: '/wfs', params: [
          service: 'WFS',
          version: '1.0.0',
          request: 'DescribeFeatureType',
          typeName: typeName
      ] )
      wfs.FeatureCollection( xmlns: "http://www.opengis.net/wfs",
          'xsi:schemaLocation': "${featureSchemaURL} http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd",
          numberOfFeatures: featureJSON?.totalFeatures, timeStamp: new Date().format( "yyy-MM-dd'T'HH:mm:ss.SSSZ" )
      ) {

        gml.boundedBy {
          gml.null( 'unknown' )
        }

        featureJSON?.features?.each { featureMember ->
          gml.featureMember {
            "${prefix}:${layerName}"( fid: "${featureMember.id}" ) {
              featureMember?.properties?.each { property ->
                "${prefix}:${property.key}"( property.value )
              }
              omar.ground_geom {
                gml.MultiPolygon( srsName: "http://www.opengis.net/gml/srs/epsg.xml#4326" ) {
                  gml.polygonMember {
                    gml.Polygon {
                      gml.outerBoundaryIs {
                        gml.LinearRing {
                          def coords = featureMember?.geometry?.coordinates[0][0].collect { it.join( ',' ) }.join( ' ' )
                          gml.coordinates( decimal: ".", cs: ",", ts: " ", coords )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x ).toString()
  }

//  def fetch()
//  {
//    def newParams = params.inject( [:] ) { a, b ->
//      switch ( b.key?.toUpperCase() )
//      {
//      case 'CONTROLLER':
//        break
//      default:
//        a[b.key] = b.value
//      }
//      a
//    }.collect { "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}" }.join( '&' )
//
//    def url = "${wfsEndpoint}?${newParams}".toURL()
//    def urlConnection = url.openConnection();
//    //def contentType = urlConnection.getHeaderField( "Content-Type" );
//    def contentType = 'text/xml'
//
//  }


  def fetchJSON(def typeName, def filter, def maxFeatures, def resultType)
  {
    def wfsParams = [
        service: 'WFS',
        version: '1.1.0',
        request: 'GetFeature',
        typeName: typeName,
        filter: filter ?: "",
        outputFormat: 'JSON',
        maxFeatures: maxFeatures,
        resultType: resultType
    ].collect {
      "${it.key}=${URLEncoder.encode( it.value as String, 'utf-8' )}"
    }.join( '&' )

    def url = "${wfsEndpoint}?${wfsParams}".toURL()

    new JsonSlurper().parse( url )
  }
}
