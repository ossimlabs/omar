predio{
   enabled  = true
   eventUrl = "http://predio.local:7070"
   queryUrl = "http://predio.local:8000"
   accessKey   = ""
   appName     = "omar_universal"
   index{
      dateField = "acquisition_date"
      idField = "id"
      expireDuration = "P3D"
      // polling interval in milliseconds
      pollingInterval = 4000
      predioUrl = "http://o2.ossim.org/o2/predio"
      wfs{
         baseUrl = "http://o2.ossim.org/o2/wfs"
         params = [SERVICE:"WFS", VERSION:"1.0.0", REQUEST:"GetFeature", typeName:"omar:raster_entry"]
         maxCount = 1000
      }
      fields = [
              categories: ["mission_id", "image_category", "product_id"],
              locations: ["country_code", "be_number"]
      ]
   }
}