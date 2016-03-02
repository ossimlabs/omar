package omar.geoscript

import org.geotools.factory.Hints

/**
 * Created by sbortman on 1/4/16.
 */
class OmarGeoScriptBootStrap
{
  def dataSourceService

  def init = { servletContext ->
    Hints.putSystemDefault( Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE )
    dataSourceService.readFromConfig()
  }

  def destroy = {
  }
}