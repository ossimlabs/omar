package omar.jpip

class JpipJob {

    String    jpipId
    String    filename
    String    entry
    String    projCode

    static constraints = {
        jpipId   ( nullable:false )
        filename ( nullable:false )
        entry    ( nullable:false )
        projCode ( nullable:false )
    }
    static mapping = {
        jpipId index: 'jpip_job_jpip_id_idx'
    }

}
