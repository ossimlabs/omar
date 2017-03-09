package three_disa


class DemGeneration {

	Date finish
	Date start
	String status


	static belongsTo = [ imageRegistration: ImageRegistration ]

	static constraints = {
		finish nullable: true
		start nullable: true
		status nullable: true
	}
}
