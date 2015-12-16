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

  static constraints = {
    name( unique: true )
  }
}
