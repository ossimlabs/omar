package omar.geoscript

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
