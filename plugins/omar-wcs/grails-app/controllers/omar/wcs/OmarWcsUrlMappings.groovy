package omar.wcs

class OmarWcsUrlMappings
{
  static mappings = {
    "/wcs/$action?"( controller: 'wcs' )
    "/wcs"( controller: 'wcs', action: 'index' )
  }
}
