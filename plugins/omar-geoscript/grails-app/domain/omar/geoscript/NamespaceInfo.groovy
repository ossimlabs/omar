package omar.geoscript

import groovy.transform.ToString

@ToString(includeNames = true)
class NamespaceInfo
{
  String prefix
  String uri

  static mapping = {
      cache true
      id generator: 'identity'
  }

  static constraints = {
    prefix( unique: true, blank: false )
    uri( url: true )
  }
}
