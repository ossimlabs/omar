package omar.geoscript

import groovy.transform.ToString

@ToString(includeNames = true, excludes = 'layerInfoList')
class WorkspaceInfo
{
  String name
  NamespaceInfo namespaceInfo
  List<LayerInfo> layerInfoList
  Map<String, String> workspaceParams

  static hasMany = [layerInfoList: LayerInfo]

  static mapping = {
      cache true
      id generator: 'identity'
  }

  static constraints = {
    name( unique: true )
  }
}
