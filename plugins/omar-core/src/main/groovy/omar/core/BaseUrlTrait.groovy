package omar.core

import grails.artefact.Enhances
import org.grails.core.artefact.ControllerArtefactHandler


/**
 * Created by gpotts on 3/1/16.
 */
@Enhances(ControllerArtefactHandler.TYPE)
trait BaseUrlTrait
{
   String getBaseUrl()
   {
      URL url = new URL (request.requestURL.toString())

      String serverURL = grailsApplication.config.grails.serverURL

      if (!serverURL) {

         serverURL = "${url.protocol}://${url.host}${url.port?':'+url.port:''}${request.contextPath?:''}"

      }

      serverURL
   }
}
