package ossimtools.app

class UrlMappings {

    static mappings = {
        "/ossimTools/$action"(controller: 'ossimTools')

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
