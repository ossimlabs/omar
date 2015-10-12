package omar.geoscript

import groovy.transform.ToString

@ToString(includeNames = true)
class NamespaceInfo
{
  String prefix
  String uri

  static constraints = {
    prefix( unique: true, blank: false )
    uri( url: true )
  }
}
