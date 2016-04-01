package omar.jpip

class JpipJob {

    String    jpipId
    String    filename
    String    entry

    static constraints = {
        jpipId (nullable:false)
        filename (nullble:false)
        entry (nullble:false)
    }
    static mapping = {
        jpipId index: 'jpip_job_jpip_id_idx'
    }

}
