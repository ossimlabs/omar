package omar.app

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import groovy.transform.ToString

/**
 * Created by sbortman on 12/24/16.
 */
@ConfigurationProperties(prefix="omar.openlayers")
@ToString(includeNames=true)
class OpenLayersConfig
{
  List<OpenLayersLayer> baseMaps

  @ToString(includeNames=true)
  static class OpenLayersLayer {
    String layerType
    String title
    String url
    HashMap<String,String> params
    HashMap<String,String> options
  }

  @ConfigurationPropertiesBinding
  static class OpenLayersLayerConverter implements Converter<Map<String, String>, OpenLayersLayer>
  {

    @Override
    OpenLayersLayer convert(Map<String, String> map)
    {
      return new OpenLayersLayer( map )
    }
  }
}
