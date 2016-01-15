/**
 * Created by sbortman on 9/3/15.
 */
package omar.core

class BindUtil
{
  static final def nonPersistent = ['class', 'constraints', 'errors', 'log', 'mapWith', 'mapping', 'properties']

  static def fixParamNames(def clazz, def params)
  {
    def names = clazz.metaClass?.properties?.grep { it.field }?.name?.sort() - nonPersistent

    def newParams = params?.inject( [:] ) { a, b ->
      def propName = names.find { it.equalsIgnoreCase( b.key ) && b.value != null }
      if ( propName )
      {
        //println "${propName}=${b.value}"
        a[propName] = b.value
      }
      else
      {
        a[b.key] = b.value
      }
      a
    }

//    println "${params} ${newParams}"

    params?.clear()
    params.putAll( newParams )
    params
  }

  static def toParamMap(def obj, def encode = false, def includeNulls = false)
  {
    def keys = obj.metaClass.properties*.name - nonPersistent
    def properties = obj.getProperties()

    def paramMap = keys.inject( [:] ) { a, b ->
      def c = properties[b]

      if ( c || includeNulls )
      {
        a[b] = ( encode ) ? URLEncoder.encode( c ?: '' as String, 'UTF-8' ) : c
      }

      a
    }

    paramMap
  }
}