package three_disa


import grails.transaction.Transactional


@Transactional
class ThreeDisaService {


    def getJobListing( params, request ) {
        def jobs
        if ( params.job.isNumber() ) { jobs = Job.findAll( max: 1) { id == params.job as Integer } }
        else { jobs = Job.findAll( max: 10, order: "desc", sort: "submitted" ) { name =~ "%${ params.job }%" } }


        return jobs
    }

	def submitJob( params, request ) {
        def demGeneration = params.demGeneration ? new DemGeneration( status: "NOT STARTED" ) : null

        def imageRegistration = new ImageRegistration(
            demGeneration: demGeneration,
            status: "NOT STARTED"
        )

        params.layers.each {
            def layer = it
            def image = new Image(
                filename: layer.filename,
                sensorModel: layer.sensorModel
            )
            layer.tiePoints.each {
                def point = it
                def tiePoint = new TiePoint(
                    filename: layer.filename,
                    x: point[0],
                    y: point[1]
                )
                image.addToTiePoints( tiePoint )
            }
            println image.properties
            imageRegistration.addToImages( image )
        }

		def job = new Job(
            bbox: params.bbox,
            imageRegistration: imageRegistration,
            name: params.name,
			submitted: new Date()
		)
        job.save()


        if ( job.hasErrors() ) {
            job.errors.allErrors.each { println it }


            return [ response: false ]
        }
        else { return [ response: true ] }
	}
}
