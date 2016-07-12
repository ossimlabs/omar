package omar.stager
import omar.core.ProcessStatus

class OmarStageFile {
    String processId
    String filename
    Boolean buildOverviews
    Boolean buildHistograms
    String overviewCompressionType
    String overviewType
    ProcessStatus status
    String statusMessage
    Date dateCreated

    static constraints = {
        processId nullable:false
        filename nullable:false
        buildOverviews nullable:true
        buildHistograms nullable:true
        overviewCompressionType nullable:true
        overviewType nullable:true
        status nullable:false
        statusMessage nullable:true
        dateCreated nullable: true
    }

    static mapping = {
        processId index: 'omar_stage_file_process_id_idx'
        filename type: 'text', index: 'omar_stage_file_filename_idx'
        status index:"omar_stage_file_status_idx"
        statusMessage type: 'text'
        dateCreated index: 'omar_stage_file_date_created_idx'
    }

    def beforeInsert() {
        if ( dateCreated == null ) dateCreated = new Date()
        if(buildOverviews==null) buildOverviews=true
        if(buildHistograms==null) buildHistograms=true
        if(!overviewCompressionType) overviewCompressionType = "NONE"
        if(!overviewType) overviewType = "ossim_tiff_box"

        true
    }
}
