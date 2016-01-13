class OmarWfsUrlMappings
{
  static mappings = {
    "/wfs/$action?"( controller: 'wfs' )
    "/wfs"( controller: 'wfs', action: 'index' )
  }
}    

