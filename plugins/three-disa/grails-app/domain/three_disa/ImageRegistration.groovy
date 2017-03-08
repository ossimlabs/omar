package three_disa


class ImageRegistration {

    Date finish
    Date start
    String status


    static belongsTo = [ job: Job ]

    static constraints = {
        demGeneration nullable: true
        finish nullable: true
        start nullable: true
    }

    static hasMany = [ tiePoints: TiePoint ]

    static hasOne = [ demGeneration: DemGeneration ]
}
