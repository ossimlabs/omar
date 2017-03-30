package three_disa


class DemGeneration {

	Date finish
	Date start
	String status


	static belongsTo = ImageRegistration

	static constraints = {
		finish nullable: true
		start nullable: true
		status nullable: true
	}
}
