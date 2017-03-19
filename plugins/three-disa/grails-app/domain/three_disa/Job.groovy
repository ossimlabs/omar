package three_disa


class Job {

	String bbox
	ImageRegistration imageRegistration
	String name
	String sensorModel
	Date submitted


	static mapping = { date index: "job_submitted_idx" }
}
