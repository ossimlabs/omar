class UrlMappings {

    static mappings = {
	"/views/**"(controller: 'views', action: 'renderView')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        // "/" {
        //     action = 'index'
        //     controller = 'omar'
        // }
        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
