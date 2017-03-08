package three_disa


import grails.transaction.Transactional


@Transactional
class ThreeDisaService {


    def getJobListing( params, request ) {
        def query = Job.list( order: "desc", sort: "submitted" )
        def jobs = query.findAll({ it.name.contains( params.jobName ?: "")  })

        def results = []
        jobs.each {
            def tiePoints = []
            it.imageRegistration.tiePoints.each {
                def tiePoint = [
                    filename: it.filename.filename,
                    x: it.x,
                    y: it.y
                ]
                tiePoints << tiePoint
            }

            results << [
                demGeneration: [
                    finish: it.imageRegistration.demGeneration?.finish,
                    start: it.imageRegistration.demGeneration?.start,
                    status: it.imageRegistration.demGeneration?.status
                ],
                imageRegistration: [
                    finish: it.imageRegistration.finish?.format( "yyyy-MM-dd HH:mm:ss" ),
                    start: it.imageRegistration.start?.format( "yyyy-MM-dd HH:mm:ss" ),
                    status: it.imageRegistration.status,
                    tiePoints: tiePoints
                ],
                name: it.name,
                sensorModel: it.sensorModel,
                submitted: it.submitted.format( "yyyy-MM-dd HH:mm:ss" )
            ]

        }


        return results
    }

	def submitJob( params, request ) {
        def demGeneration = params.demGeneration ? new DemGeneration( status: "NOT STARTED" ) : null

        def imageRegistration = new ImageRegistration(
            demGeneration: demGeneration,
            status: "NOT STARTED"
        )

        params.layers.each {
            def layer = it

            def filename = Filename.findOrSaveWhere( filename: layer.filename )
            layer.tiePoints.each {
                def point = it
                def tiePoint = new TiePoint(
                    filename: filename,
                    x: point[0],
                    y: point[1]
                )

                imageRegistration.addToTiePoints( tiePoint )
            }
        }

		def job = new Job(
            imageRegistration: imageRegistration,
            name: params.name,
            sensorModel: params.sensorModel,
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
