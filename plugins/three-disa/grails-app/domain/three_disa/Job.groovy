package three_disa


class Job {

	String bbox
	ImageRegistration imageRegistration
	String name
	Date submitted


	static mapping = { date index: "job_submitted_idx" }
}
