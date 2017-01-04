package omar.app

import omar.openlayers.OmarOpenlayersUtils
import org.springframework.beans.factory.InitializingBean
import grails.converters.JSON

class OmarController implements InitializingBean
{
  def openlayers

  OpenLayersConfig openLayersConfig

  def index()
  {
      println openLayersConfig

  		def clientConfig = new ConfigObject()
  		clientConfig.params = grailsApplication.config.omar.app

  		//clientConfig.openlayers = OmarOpenlayersUtils.openlayersConfig
      clientConfig.openlayers = openlayers

  		 // Use Enhancer traits from omar-core getBaseUrl()
  		clientConfig.serverURL = getBaseUrl()

  		// Params to pass to client
  		def clientParams = grailsApplication.config.omar.app

  		[
  				clientConfig: clientConfig
  		]
	}

  void afterPropertiesSet() throws Exception
  {
      openlayers = grailsApplication.config.omar.openlayers

      //  Collect baseMaps[x] named params as a list and use it to override baseMaps
      def newBaseMaps = openlayers.keySet()?.grep { it ==~ /baseMaps\[\d+\]/ }?.collect { openlayers[it] }

      if ( newBaseMaps )
      {
          openlayers.baseMaps = newBaseMaps
      }

      // println openlayers as JSON
  }
}
