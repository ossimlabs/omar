package omar.wfs

import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.filter.Function
import geoscript.geom.GeometryCollection
import geoscript.workspace.Workspace

import grails.transaction.Transactional
import omar.core.HttpStatus
import omar.geoscript.LayerInfo
import omar.geoscript.NamespaceInfo
import omar.geoscript.WorkspaceInfo

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.xml.StreamingMarkupBuilder

import geoscript.layer.io.CsvWriter
@Transactional( readOnly = true )
class WebFeatureService
{
  def grailsLinkGenerator
  def geoscriptService

  static final Map<String, String> typeMappings = [
          'Double': 'xsd:double',
          'Integer': 'xsd:int',
          'Long': 'xsd:long',
          'Polygon': 'gml:PolygonPropertyType',
          'MultiPolygon': 'gml:MultiPolygonPropertyType',
          'MultiLineString': 'gml:MultiLineStringPropertyType',
          'String': 'xsd:string',
          'java.lang.Boolean': 'xsd:boolean',
          'java.math.BigDecimal': 'xsd:decimal',
          'java.sql.Timestamp': 'xsd:dateTime'
  ]


  static final Map<String, String> ogcNamespacesByPrefix = [
          // These are OGC/XML specs
          gml: "http://www.opengis.net/gml",
          ogc: "http://www.opengis.net/ogc",
          ows: "http://www.opengis.net/ows",
          wfs: "http://www.opengis.net/wfs",
          xlink: "http://www.w3.org/1999/xlink",
          xs: "http://www.w3.org/2001/XMLSchema",
          xsi: "http://www.w3.org/2001/XMLSchema-instance",
  ]

  static final List<String> outputFormats = [
          'text/xml; subtype=gml/3.1.1',
          'GML2',
          'KML',
          'SHAPE-ZIP',
          'application/gml+xml; version=3.2',
          'application/json',
          'application/vnd.google-earth.kml xml',
          'application/vnd.google-earth.kml+xml',
          'csv',
          'gml3',
          'gml32',
          'json',
          'text/xml; subtype=gml/2.1.2',
          'text/xml; subtype=gml/3.2'
  ]

  static final List<String> geometryOperands = [
          'gml:Envelope',
          'gml:Point',
          'gml:LineString',
          'gml:Polygon'
  ]

  static final List<String> spatialOperators = [
          "Disjoint",
          "Equals",
          "DWithin",
          "Beyond",
          "Intersects",
          "Touches",
          "Crosses",
          "Within",
          "Contains",
          "Overlaps",
          "BBOX"
  ]

  static final List<String> comparisonOperators = [
          'LessThan',
          'GreaterThan',
          'LessThanEqualTo',
          'GreaterThanEqualTo',
          'EqualTo',
          'NotEqualTo',
          'Like',
          'Between',
          'NullCheck'
  ]

  def getCapabilities(GetCapabilitiesRequest wfsParams)
  {
    HashMap result = [status: HttpStatus.OK]

    try
    {
      def wfsServiceAddress = grailsLinkGenerator.link( absolute: true, uri: '/wfs' )
      def wfsSchemaLocation = grailsLinkGenerator.link( absolute: true, uri: '/schemas/wfs/1.1.0/wfs.xsd' )
      def featureTypeNamespacesByPrefix = NamespaceInfo.list().inject( [:] ) { a, b -> a[b.prefix] = b.uri; a }
      def functionNames = geoscriptService.listFunctions2()
      def x = {
        mkp.xmlDeclaration()
        mkp.declareNamespace( ogcNamespacesByPrefix )
        mkp.declareNamespace( featureTypeNamespacesByPrefix )
        wfs.WFS_Capabilities( version: "1.1.0", xmlns: "http://www.opengis.net/wfs",
                'xsi:schemaLocation': "http://www.opengis.net/wfs ${wfsSchemaLocation}"
        ) {
          ows.ServiceIdentification {
            ows.Title( 'O2 WFS Server' ) // Put in config
            ows.Abstract( 'O2 WFS server' ) // Put in config
            ows.Keywords {
              def keywords = ['WFS', 'WMS', 'OMAR'] // Put in config
              keywords.each { keyword ->
                ows.Keyword( keyword )
              }
            }
            ows.ServiceType( 'WFS' )
            ows.ServiceTypeVersion( '1.1.0' ) // Put in config?
            ows.Fees( 'NONE' )
            ows.AccessConstraints( 'NONE' )
          }
          ows.ServiceProvider {
            ows.ProviderName( 'OSSIM Labs' )  // Put in config?
            ows.ServiceContact {
              ows.IndividualName( 'Scott Bortman' ) // Put in config?
              ows.PositionName( 'OMAR Developer' ) // Put in config?
              ows.ContactInfo {
                ows.Phone {
                  ows.Voice()
                  ows.Facsimile()
                }
                ows.Address {
                  ows.DeliveryPoint()
                  ows.City()
                  ows.AdministrativeArea()
                  ows.PostalCode()
                  ows.Country()
                  ows.ElectronicMailAddress()
                }
              }
            }
          }
          ows.OperationsMetadata {
            ows.Operation( name: "GetCapabilities" ) {
              ows.DCP {
                ows.HTTP {
                  ows.Get( 'xlink:href': wfsServiceAddress )
                  ows.Post( 'xlink:href': wfsServiceAddress )
                }
              }
              ows.Parameter( name: "AcceptVersions" ) {
                //ows.Value( '1.0.0' )
                ows.Value( '1.1.0' )
              }
              ows.Parameter( name: "AcceptFormats" ) {
                ows.Value( 'text/xml' )
              }
            }
            ows.Operation( name: "DescribeFeatureType" ) {
              ows.DCP {
                ows.HTTP {
                  ows.Get( 'xlink:href': wfsServiceAddress )
                  ows.Post( 'xlink:href': wfsServiceAddress )
                }
              }
              ows.Parameter( name: "outputFormat" ) {
                ows.Value( 'text/xml; subtype=gml/3.1.1' )
              }
            }
            ows.Operation( name: "GetFeature" ) {
              ows.DCP {
                ows.HTTP {
                  ows.Get( 'xlink:href': wfsServiceAddress )
                  ows.Post( 'xlink:href': wfsServiceAddress )
                }
              }
              ows.Parameter( name: "resultType" ) {
                ows.Value( 'results' )
                ows.Value( 'hits' )
              }
              ows.Parameter( name: "outputFormat" ) {
                outputFormats.each { outputFormat ->
                  ows.Value( outputFormat )
                }
              }
              // ows.Constraint( name: "LocalTraverseXLinkScope" ) {
              //   ows.Value( 2 )
              // }
            }
            // ows.Operation( name: "GetGmlObject" ) {
            //   ows.DCP {
            //     ows.HTTP {
            //       ows.Get( 'xlink:href': wfsServiceAddress )
            //       ows.Post( 'xlink:href': wfsServiceAddress )
            //     }
            //   }
            // }
            // ows.Operation( name: "LockFeature" ) {
            //   ows.DCP {
            //     ows.HTTP {
            //       ows.Get( 'xlink:href': wfsServiceAddress )
            //       ows.Post( 'xlink:href': wfsServiceAddress )
            //     }
            //   }
            //   ows.Parameter( name: "releaseAction" ) {
            //     ows.Value( 'ALL' )
            //     ows.Value( 'SOME' )
            //   }
            // }
            // ows.Operation( name: "GetFeatureWithLock" ) {
            //   ows.DCP {
            //     ows.HTTP {
            //       ows.Get( 'xlink:href': wfsServiceAddress )
            //       ows.Post( 'xlink:href': wfsServiceAddress )
            //     }
            //   }
            //   ows.Parameter( name: "resultType" ) {
            //     ows.Value( 'results' )
            //     ows.Value( 'hits' )
            //   }
            //   ows.Parameter( name: "outputFormat" ) {
            //   	outputFormats.each { outputFormat ->
            //     	ows.Value( outputFormat )
            //   	}
            //   }
            // }
            // ows.Operation( name: "Transaction" ) {
            //   ows.DCP {
            //     ows.HTTP {
            //       ows.Get( 'xlink:href': wfsServiceAddress )
            //       ows.Post( 'xlink:href': wfsServiceAddress )
            //     }
            //   }
            //   ows.Parameter( name: "inputFormat" ) {
            //     ows.Value( 'text/xml; subtype=gml/3.1.1' )
            //   }
            //   ows.Parameter( name: "idgen" ) {
            //     ows.Value( 'GenerateNew' )
            //     ows.Value( 'UseExisting' )
            //     ows.Value( 'ReplaceDuplicate' )
            //   }
            //   ows.Parameter( name: "releaseAction" ) {
            //     ows.Value( 'ALL' )
            //     ows.Value( 'SOME' )
            //   }
            // }
          }
          FeatureTypeList {
            Operations {
              Operation( 'Query' )
              // Operation( 'Insert' )
              // Operation( 'Update' )
              // Operation( 'Delete' )
              // Operation( 'Lock' )
            }
            LayerInfo.list()?.each { layerInfo ->
              WorkspaceInfo workspaceInfo = WorkspaceInfo.findByName( layerInfo.workspaceInfo.name )

              Workspace.withWorkspace( geoscriptService.getWorkspace( workspaceInfo?.workspaceParams ) ) { Workspace workspace ->
                try
                {

                  def layer = workspace[layerInfo.name]
                  def uri = layer?.schema?.uri
                  def prefix = NamespaceInfo.findByUri( uri )?.prefix
                  def geoBounds = ( layer?.proj?.epsg == 4326 ) ? layer?.bounds : layer?.bounds?.reproject( 'epsg:4326' )
                  FeatureType( "xmlns:${prefix}": uri ) {
                    Name( "${prefix}:${layerInfo.name}" )
                    Title( layerInfo.title )
                    Abstract( layerInfo.description )
                    ows.Keywords {
                      layerInfo.keywords?.each { keyword ->
                        ows.Keyword( keyword )
                      }
                    }
                    DefaultSRS( "urn:x-ogc:def:crs:${layer?.proj?.id}" )
                    ows.WGS84BoundingBox {
                      ows.LowerCorner( "${geoBounds?.minX} ${geoBounds?.minY}" )
                      ows.UpperCorner( "${geoBounds?.maxX} ${geoBounds?.maxY}" )
                    }
                  }
                }
                catch ( e )
                {
                  println e.message
                }
              }
            }
          }
          ogc.Filter_Capabilities {
            ogc.Spatial_Capabilities {
              ogc.GeometryOperands {
                geometryOperands.each { geometryOperand ->
                  ogc.GeometryOperand( geometryOperand )
                }
              }
              ogc.SpatialOperators {
                spatialOperators.each { spatialOperator ->
                  ogc.SpatialOperator( name: spatialOperator )
                }
              }
            }
            ogc.Scalar_Capabilities {
              ogc.LogicalOperators()
              ogc.ComparisonOperators {
                comparisonOperators.each { comparisonOperator ->
                  ogc.ComparisonOperator( comparisonOperator )
                }
              }
              ogc.ArithmeticOperators {
                ogc.SimpleArithmetic()
                ogc.Functions {
                  ogc.FunctionNames {
                    functionNames.each { functionName ->
                      ogc.FunctionName( nArgs: functionName.argCount, functionName.name )
                    }
                  }
                }
              }
            }
            ogc.Id_Capabilities {
              ogc.FID()
              ogc.EID()
            }
          }
        }
      }
      def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )
      def contentType = 'application/xml'

      result.contentType = contentType
      result.buffer = "${xml}".toString()
    }
    catch ( e )
    {
      result.contentType = "plain/text"
      result.buffer = "Error: ${e.toString()}"
      result.status = HttpStatus.INTERNAL_SERVER_ERROR
    }

    result
  }


  private def generateSchema(Schema schema, String prefix, String schemaLocation)
  {
    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace(
              gml: 'http://www.opengis.net/gml',
              "${prefix}": schema.uri,
              xsd: 'http://www.w3.org/2001/XMLSchema'
      )
      xsd.schema( elementFormDefault: 'qualified', targetNamespace: schema.uri ) {
        xsd.import( namespace: 'http://www.opengis.net/gml', schemaLocation: "${schemaLocation}/schemas/gml/3.1.1/base/gml.xsd" )
        xsd.complexType( name: "${schema.name}Type" ) {
          xsd.complexContent {
            xsd.extension( base: 'gml:AbstractFeatureType' ) {
              xsd.sequence {
                schema.fields.each { field ->
                  def descr = schema.featureType.getDescriptor( field.name )
                  xsd.element( maxOccurs: "${descr.maxOccurs}", minOccurs: "${descr.minOccurs}",
                          name: "${field.name}", nillable: "${descr.nillable}",
                          type: "${typeMappings.get( field.typ, field.typ )}" )
                }
              }
            }
          }
        }
        xsd.element( name: schema.name, substitutionGroup: 'gml:_Feature', type: "${prefix}:${schema.name}Type" )
      }
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    xml.toString()
  }

  private def generateHitCount(def hitCount, def namespaceInfo)
  {
    def namespaces = [
            gml: "http://www.opengis.net/gml",
            ogc: "http://www.opengis.net/ogc",
            ows: "http://www.opengis.net/ows",
            wfs: "http://www.opengis.net/wfs",
            xlink: "http://www.w3.org/1999/xlink",
            xs: "http://www.w3.org/2001/XMLSchema",
            xsi: "http://www.w3.org/2001/XMLSchema-instance",
    ]

    namespaces[namespaceInfo.prefix] = namespaceInfo.uri

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( namespaces )
      wfs.FeatureCollection(
              numberOfFeatures: hitCount,
              timeStamp: new Date().format( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone( 'GMT' ) ),
              'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd"
      )
    }

    def xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )

    xml.toString()
  }

  def describeFeatureType(DescribeFeatureTypeRequest wfsParams)
  {
    HashMap result = [status: HttpStatus.OK]
    def layerInfo = geoscriptService.findLayerInfo( wfsParams )
    String schemaLocation = grailsLinkGenerator.serverBaseURL
    def xml = null

    try
    {
      Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo?.workspaceInfo?.workspaceParams ) ) {
        Workspace workspace ->

          Schema schema = workspace[layerInfo.name].schema
          String prefix = NamespaceInfo.findByUri( schema.uri ).prefix

          xml = generateSchema( schema, prefix, schemaLocation )
      }

      result.contentType = "text/xml"
      result.buffer = xml
    }
    catch ( e )
    {
      result.status = HttpStatus.INTERNAL_SERVER_ERROR
      result.contentType = "plain/text"
      result.buffer = "${e}"
    }

//    println xml

    result
  }

  def getFeature(GetFeatureRequest wfsParams)
  {
    HashMap result = [status: HttpStatus.OK, buffer: "", contentType: "text/xml", buffer: ""]

    switch ( wfsParams?.outputFormat?.toUpperCase() )
    {
      case 'GML3':
        try
        {
          def buffer = getFeatureGML3( wfsParams )
          result.contentType = 'text/xml'
          result.filename = "wfs.xml"
          result.buffer = buffer
        }
        catch ( e )
        {
          result.contentType = "plain/text"
          result.buffer = "${e}"
          result.status = HttpStatus.INTERNAL_SERVER_ERROR
        }
        break
      case 'JSON':
      case 'GEOJSON':
        try
        {
          def buffer = getFeatureJSON( wfsParams )
          result.contentType = 'application/json'
          result.buffer = buffer
          result.filename = "wfs.json"
        }
        catch ( e )
        {
          result.contentType = "plain/text"
          result.buffer = "${e}"
          result.status = HttpStatus.INTERNAL_SERVER_ERROR
        }
        break
      case 'KML':
        try
        {
          def buffer = getFeatureKML( wfsParams )
          result.contentType = 'application/vnd.google-earth.kml+xml'
          result.filename = "wfs.kml"
          result.buffer = buffer
        }
        catch ( e )
        {
          e.printStackTrace()
          result.contentType = "plain/text"
          result.buffer = "${e}"
          result.status = HttpStatus.INTERNAL_SERVER_ERROR
        }
        break;
      case 'CSV':
        try
        {
          def buffer = getFeatureCsv( wfsParams )
          result.contentType = 'text/csv'
          result.filename = "wfs.csv"
          result.buffer = buffer
        }
        catch ( e )
        {
          e.printStackTrace()
          result.contentType = "plain/text"
          result.buffer = "${e}"
          result.status = HttpStatus.INTERNAL_SERVER_ERROR
        }
        break;
      default:
        try
        {
          def buffer = getFeatureGML3( wfsParams )
          result.contentType = 'text/xml'
          //result.filename = "wfs.xml"
          result.buffer = buffer
        }
        catch ( e )
        {
          result.contentType = "plain/text"
          result.buffer = "${e}"
          result.status = HttpStatus.INTERNAL_SERVER_ERROR
        }
        break
    }

    result
  }

  private def getFeatureJSON(GetFeatureRequest wfsParams)
  {
    def layerInfo = geoscriptService.findLayerInfo( wfsParams )
    def results


    def options = geoscriptService.parseOptions( wfsParams )

    //println options
    println layerInfo.workspaceInfo.workspaceParams
    Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->
      def layer = workspace[layerInfo.name]
      def count = layer.count( wfsParams.filter ?: Filter.PASS )//wfsParams.filter )

//      println features

      def features = layer.collectFromFeature( options ) { feature ->
        switch ( wfsParams?.outputFormat?.toUpperCase() )
        {
          case 'GML3':
            return feature.getGml( version: 3, format: false, bounds: false, xmldecl: false, nsprefix: prefix )
            break
          case 'GEOJSON':
          case 'JSON':
            return new JsonSlurper().parseText( feature.geoJSON )
            break
          default:
            return feature.getGml( version: 3, format: false, bounds: false, xmldecl: false, nsprefix: prefix )
        }
      }

      results = [
              crs: [
                      properties: [
                              name: "urn:ogc:def:crs:${layer.proj.id}"
                      ],
                      type: "name"
              ],
              totalFeatures: count,
              features: features,
//          features: [],
              type: "FeatureCollection"
      ]

      workspace.close()
    }

    return JsonOutput.toJson( results )
  }

  private def getFeatureKML(GetFeatureRequest wfsParams)
  {
    def layerInfo = geoscriptService.findLayerInfo( wfsParams )
    def result


    def options = geoscriptService.parseOptions( wfsParams )

    //println options

    Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->
      def layer = workspace[layerInfo.name]
      def count = layer.count( wfsParams.filter ?: Filter.PASS )//wfsParams.filter )
      def nameClosure = {fields->

        if((fields.title!=null) && (fields.title.trim()!="")) return fields.title?.trim()

        return new File(fields?.filename).name
      }
      result = ExportKml.toKMLString(layer, nameClosure)

      workspace.close()
    }


    result
  }

  private def getFeatureCsv(GetFeatureRequest wfsParams)
  {
    def layerInfo = geoscriptService.findLayerInfo( wfsParams )
    def result


    def options = geoscriptService.parseOptions( wfsParams )

    //println options
    def writer = new CsvWriter()
    Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo.workspaceInfo.workspaceParams ) ) { workspace ->
      def layer = workspace[layerInfo.name]
      def count = layer.count( wfsParams.filter ?: Filter.PASS )//wfsParams.filter )
      def nameClosure = {fields->

        if((fields.title!=null) && (fields.title.trim()!="")) return fields.title?.trim()

        return new File(fields?.filename).name
      }
      result = writer.write(layer)

      workspace.close()
    }


    result
  }

  private def getFeatureGML3(GetFeatureRequest wfsParams)
  {
    def layerInfo = geoscriptService.findLayerInfo( wfsParams )
    def xml

//    println wfsParams


    def options = geoscriptService.parseOptions( wfsParams )

//    println options

    //println layerInfo?.workspaceInfo?.workspaceParams

    Workspace.withWorkspace( geoscriptService.getWorkspace( layerInfo?.workspaceInfo?.workspaceParams ) ) { workspace ->
      def layer = workspace[layerInfo.name]
      def matched = layer?.count( wfsParams.filter ?: Filter.PASS )
      def count = ( wfsParams.maxFeatures ) ? Math.min( matched, wfsParams.maxFeatures ) : matched
      def namespaceInfo = layerInfo?.workspaceInfo?.namespaceInfo

      def schemaLocations = [
              namespaceInfo.uri,
              grailsLinkGenerator.link( absolute: true, uri: '/wfs', params: [
                      service: 'WFS', version: wfsParams.version, request: 'DescribeFeatureType', typeName: wfsParams.typeName
              ] ),
              "http://www.opengis.net/wfs",
              grailsLinkGenerator.link( absolute: true, uri: '/schemas/wfs/1.1.0/wfs.xsd' )
      ]

//      def features = layer.getFeatures( options )
      def features = layer.collectFromFeature( options ) { feature ->
        switch ( wfsParams?.outputFormat?.toUpperCase() )
        {
          case 'GML3':
            return feature.getGml( version: 3, format: false, bounds: false, xmldecl: false, nsprefix: namespaceInfo.prefix )
            break
          case 'GEOJSON':
          case 'JSON':
            return new JsonSlurper().parseText( feature.geoJSON )
            break
          default:
            return feature.getGml( version: 3, format: false, bounds: false, xmldecl: false, nsprefix: namespaceInfo.prefix )
        }
      }

      def x = {
        mkp.xmlDeclaration()
        mkp.declareNamespace( ogcNamespacesByPrefix )
        mkp.declareNamespace( "${namespaceInfo.prefix}": namespaceInfo.uri )

        wfs.FeatureCollection(
                numberOfFeatures: count,
                timeStamp: new Date().format( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone( 'GMT' ) ),
                'xsi:schemaLocation': schemaLocations.join( ' ' ),
                numberMatched: matched,
                startIndex: wfsParams.startIndex ?: '0'
        ) {
          if ( !( wfsParams?.resultType?.toLowerCase() == 'hits' ) )
          {
            gml.featureMembers {
              features?.each { feature ->
                mkp.yieldUnescaped(
                        // writer.write( feature, 3, false, false, false, namespaceInfo.prefix )
                        feature
                )
              }
            }
          }
        }
      }

      xml = new StreamingMarkupBuilder( encoding: 'utf-8' ).bind( x )
    }

    return xml
  }

  def foobar()
  {
    Function.registerFunction( "queryCollection" ) { def layerName, def attributeName, def filter ->
      def workspace = getWorkspace( 'omar' )
      def results = workspace[layerName].collectFromFeature( filter ) { it[attributeName] }
      workspace?.close()
      results
    }

    Function.registerFunction( 'collectGeometries' ) { def geometries ->
      def multiType = ( geometries ) ? "geoscript.geom.Multi${geometries[0].class.simpleName}" : new GeometryCollection( geometries )

      Class.forName( multiType ).newInstance( geometries )
    }
  }
}
