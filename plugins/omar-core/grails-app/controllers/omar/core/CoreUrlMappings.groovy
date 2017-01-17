package omar.core

class CoreUrlMappings {

   static mappings = {
      "/$controller/$action?/$id?(.$format)?"{
         constraints {
            // apply constraints here
         }
      }
      "/api" (controller: "apis")
      "/api/index" (controller: "apis")


   }
}
