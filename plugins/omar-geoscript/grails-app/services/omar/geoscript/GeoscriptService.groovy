package omar.geoscript

import grails.transaction.Transactional

@Transactional( readOnly = true )
class GeoscriptService
{
	def parseOptions( def wfsParams )
	{
		def wfsParamNames = [
				'maxFeatures', 'startIndex', 'propertyName', 'sortBy', 'filter'
		]

		def options = wfsParamNames.inject( [ : ] ) { options, wfsParamName ->
			if ( wfsParams[ wfsParamName ] != null )
			{
				switch ( wfsParamName )
				{
				case 'maxFeatures':
					options[ 'max' ] = wfsParams[ wfsParamName ]
					break
				case 'startIndex':
					options[ 'start' ] = wfsParams[ wfsParamName ]
					break
				case 'propertyName':
					options[ 'fields' ] = wfsParams[ wfsParamName ]?.split( ',' )?.collect {
						it.split( ':' )?.last()
					} as List<String>
					break
				case 'sortBy':
					if ( wfsParams[ wfsParamName ]?.trim() )
					{
						options[ 'sort' ] = wfsParams[ wfsParamName ].split( ',' )?.collect {
							def props = it.split( ' ' ) as List
							if ( props.size() == 2 )
							{
								props[ 1 ] = ( props[ 1 ].equalsIgnoreCase( 'D' ) ) ? 'DESC' : 'ASC'
							}
							props
						}
					}
					break
				default:
					options[ wfsParamName ] = wfsParams[ wfsParamName ]
				}
			}
			options
		}
		options
	}

	def findLayerInfo( def wfsParams )
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
			( namespacePrefix, layerName ) = x
			break
		}

		def namespaceInfo

		if ( wfsParams?.namespace )
		{
			def pattern = /xmlns\(\w+=(.*)\)/
			def matcher = wfsParams?.namespace =~ pattern

			if ( matcher )
			{
				def uri = matcher[ 0 ][ 1 ]

				namespaceInfo = NamespaceInfo.findByUri( uri )
			}
			else
			{
				println "${ '*' * 20 } No Match ${ '*' * 20 }"
			}

			layerName = wfsParams?.typeName?.split( ':' )?.last()
		}
		else
		{
			namespaceInfo = NamespaceInfo.findByPrefix( namespacePrefix )
		}

		println "${namespaceInfo} ${layerName}"

		LayerInfo.where {
			name == layerName && workspaceInfo.namespaceInfo == namespaceInfo
		}.get()
	}
}
