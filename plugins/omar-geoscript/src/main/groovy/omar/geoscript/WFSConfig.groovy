package omar.geoscript

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import groovy.transform.ToString

/**
 * Created by sbortman on 12/26/16.
 */
@ConfigurationProperties( 'wfs' )
@ToString(includeNames=true)
class WFSConfig
{
  List<FeatureTypeNamespace> featureTypeNamespaces
  List<Datastore> datastores
  List<FeatureType> featureTypes

  @ToString(includeNames=true)
  static class FeatureTypeNamespace
  {
    String prefix
    String uri
  }

  @ToString(includeNames=true)
  static class Datastore
  {
    String namespaceId
    String datastoreId
    Map<String, String> datastoreParams
  }

  @ToString(includeNames=true)
  static class FeatureType
  {
    String name
    String title
    String description
    List<String> keywords
    String datastoreId
  }

  @ConfigurationPropertiesBinding
  static class FeatureTypeNamespaceConverter implements Converter<Map<String, String>, FeatureTypeNamespace>
  {

    @Override
    FeatureTypeNamespace convert(Map<String, String> map)
    {
      return new FeatureTypeNamespace( map )
    }
  }

  @ConfigurationPropertiesBinding
  static class DatastoreConverter implements Converter<Map<String, Object>, Datastore>
  {

    @Override
    Datastore convert(Map<String, Object> map)
    {
      return new Datastore( map )
    }
  }

  @ConfigurationPropertiesBinding
  static class FeatureTypeConverter implements Converter<Map<String,Object>, FeatureType>
  {

    @Override
    FeatureType convert(Map<String, Object> map)
    {
      return new FeatureType(map)
    }
  }
}
