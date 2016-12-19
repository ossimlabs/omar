package ingest.metrics.app

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$ingestId?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
