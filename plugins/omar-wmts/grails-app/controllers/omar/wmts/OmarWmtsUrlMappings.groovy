package omar.wmts

class OmarWmtsUrlMappings
{
   static mappings = {
      "/wmts/$action?"( controller: 'wmts' )
      "/wmts"( controller: 'wmts', action: 'index' )
   }
}
