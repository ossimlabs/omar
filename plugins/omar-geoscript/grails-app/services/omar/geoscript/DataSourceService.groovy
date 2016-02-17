package omar.geoscript

import grails.transaction.Transactional

@Transactional
class DataSourceService
{
	def grailsApplication
	def messageSource

	def readFromConfig()
	{
		if ( NamespaceInfo.count() == 0 )
		{
			grailsApplication.config.wfs.featureTypeNamespaces.each {
				NamespaceInfo.findOrSaveByPrefixAndUri( it.prefix, it.uri )
			}

			grailsApplication.config.wfs.datastores.each { datastore ->
				def workspaceInfo = WorkspaceInfo.findOrCreateByName( datastore.datastoreId )

				workspaceInfo.with {
					namespaceInfo = NamespaceInfo.findByPrefix( datastore.namespaceId )
					workspaceParams = datastore.datastoreParams
					save()
				}

				if ( workspaceInfo.hasErrors() )
				{
					workspaceInfo.errors.allErrors.each { println messageSource.getMessage( it, null ) }
				}
			}

			grailsApplication.config.wfs.featureTypes.each { featureType ->
				WorkspaceInfo.withTransaction {
					def workspaceInfo = WorkspaceInfo.findByName( featureType.datastoreId )
					def layerInfo = LayerInfo.findOrCreateByNameAndWorkspaceInfo( featureType.name, workspaceInfo )

					layerInfo.with {
						title = featureType.title
						description = featureType.description
						keywords = featureType.keywords
					}

					workspaceInfo.addToLayerInfoList( layerInfo )
					workspaceInfo.save()

					if ( workspaceInfo.hasErrors() )
					{
						workspaceInfo.errors.allErrors.each { println messageSource.getMessage( it, null ) }
					}
				}
			}
		}
	}
}
