package three_disa


class Image {

    String filename
    String sensorModel


    static belongsTo = ImageRegistration

    static hasMany = [ tiePoints: TiePoint ]
}
