predio{
   index{
      // polling interval in milliseconds
      pollingInterval = 4000
      predioUrl = ""
      wfs{
         url = ""
      }
      idField = "id"
      fields = [
         categories: ["mission_id", "image_category", "product_id"],
         locations: ["country_code", "be_number"]
      ]
   }
}