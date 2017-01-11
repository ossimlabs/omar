package omar.geoscript

import groovy.transform.ToString

@ToString(includeNames = true, excludes = 'workspaceInfo')
class LayerInfo
{
  String name
  String title
  String description
  String[] keywords

  static belongsTo = [workspaceInfo: WorkspaceInfo]

  static mapping = {
      cache true
      id generator: 'identity'
  }

  static constraints = {
    name()
    title()
    description()
    keywords()
  }
}
