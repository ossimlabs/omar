package omar.geoscript

import groovy.transform.ToString

@ToString(includeNames = true)
class LayerInfo
{
  String name
  String title
  String description
  String[] keywords

  static belongsTo = [workspaceInfo: WorkspaceInfo]

  static constraints = {
    name()
    title()
    description()
    keywords()
  }
}
