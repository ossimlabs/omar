package three_disa


class Job {

	String name
	String sensorModel
	Date submitted


	static hasOne = [ imageRegistration: ImageRegistration ]

	static mapping = { date index: "job_submitted_idx" }
}
