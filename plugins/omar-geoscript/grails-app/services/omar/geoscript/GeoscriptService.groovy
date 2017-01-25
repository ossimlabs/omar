package omar.geoscript

import geoscript.filter.Function
import geoscript.geom.GeometryCollection
import geoscript.workspace.Workspace
import grails.transaction.Transactional
import org.geotools.data.DataStoreFinder
import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.capability.FunctionName
import org.springframework.beans.factory.InitializingBean

@Transactional( readOnly = true )
class GeoscriptService implements InitializingBean
{
  def parseOptions(def wfsParams)
  {
    def wfsParamNames = [
        'maxFeatures', 'startIndex', 'propertyName', 'sortBy', 'filter'
    ]

    def options = wfsParamNames.inject( [:] ) { options, wfsParamName ->
      if ( wfsParams[wfsParamName] != null )
      {
        switch ( wfsParamName )
        {
        case 'maxFeatures':
          options['max'] = wfsParams[wfsParamName]
          break
        case 'startIndex':
          options['start'] = wfsParams[wfsParamName]
          break
        case 'propertyName':
          def fields =  wfsParams[wfsParamName]?.split( ',' )?.collect {
            it.split( ':' )?.last()
          } as List<String>
          if ( fields && ! fields?.isEmpty() && fields?.every { it } ) {
            // println "FIELDS: ${fields.size()}"
            options['fields'] = fields
          }
          break
        case 'sortBy':
          if ( wfsParams[wfsParamName]?.trim() )
          {
            options['sort'] = wfsParams[wfsParamName].split( ',' )?.collect {
              def props = it.split( ' ' ) as List
              if ( props.size() == 2 )
              {
                props[1] = ( props[1].equalsIgnoreCase( 'D' ) ) ? 'DESC' : 'ASC'
              }
              props
            }
          }
          break
        default:
          if ( wfsParams[wfsParamName] ) {
            options[wfsParamName] = wfsParams[wfsParamName]
          }
        }
      }
      options
    }
    options
  }

  def findLayerInfo(def wfsParams)
  {
    def x = wfsParams?.typeName?.split( ':' )
    def namespacePrefix
    def layerName

    switch ( x?.size() )
    {
    case 1:
      layerName = x?.last()
      break
    case 2:
      (namespacePrefix, layerName) = x
      break
    }

    def namespaceInfo

    if ( !namespacePrefix && wfsParams?.namespace )
    {
      def pattern = /xmlns\(\w+=(.*)\)/
      def matcher = wfsParams?.namespace =~ pattern

      if ( matcher )
      {
        def uri = matcher[0][1]

        namespaceInfo = NamespaceInfo.findByUri( uri )
      }
      else
      {
        println "${'*' * 20} No Match ${'*' * 20}"
      }

      layerName = wfsParams?.typeName?.split( ':' )?.last()
    }
    else
    {
      namespaceInfo = NamespaceInfo.findByPrefix( namespacePrefix )
    }

    //println "${namespaceInfo} ${layerName}"

    LayerInfo.where {
      name == layerName && workspaceInfo.namespaceInfo == namespaceInfo
    }.get()
  }

  private def getWorkspaceAndLayer(String layerName)
  {
    def layerInfo = findLayerInfo( [typeName: layerName] )
    def workspace = getWorkspace( layerInfo?.workspaceInfo?.workspaceParams )
    def layer = workspace[layerInfo?.name]

    [workspace, layer]
  }

  def listFunctions2()
  {
    List names = []
    CommonFactoryFinder.getFunctionFactories().each { f ->
      f.functionNames.each { fn ->
        if ( fn instanceof FunctionName )
        {
          names << [name: fn.functionName.toString(), argCount: fn.argumentCount]
        }
      }
    }
    names.sort { a, b -> a.name.compareToIgnoreCase b.name }
  }

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
  }

  Workspace getWorkspace(Map params)
  {
    def dataStore = DataStoreFinder.getDataStore( params )

    ( dataStore ) ? new Workspace( dataStore ) : null
  }

}
