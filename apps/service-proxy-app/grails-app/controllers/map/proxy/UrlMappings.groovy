package map.proxy

class UrlMappings {

    static mappings = {
        "/wmsProxy"(controller: 'wmsProxy', action: 'index')
        
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
