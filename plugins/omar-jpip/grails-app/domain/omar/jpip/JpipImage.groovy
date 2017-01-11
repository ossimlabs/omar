package omar.jpip

import java.sql.Timestamp

class JpipImage {
    String    jpipId
    String    filename
    String    entry
    String    projCode
    Timestamp requestDate
    Timestamp createDate
    String    status

    static constraints = {
        jpipId      ( nullable:false )
        filename    ( nullable:false )
        entry       ( nullable:false )
        projCode    ( nullable:false )
        requestDate ( nullable:true )
        createDate  ( nullable:true )
        status      ( nullable:false )
    }

    static mapping = {
        cache true
        id generator: 'identity'
        jpipId      index: 'jpip_image_jpip_id_idx'
        filename    index: 'jpip_image_filename_idx'
        entry       index: 'jpip_image_entry_idx'
        projCode    index: 'jpip_image_proj_code_idx'
        requestDate index: 'jpip_image_request_date_idx', sqlType: "timestamp with time zone"
        createDate  index: 'jpip_image_create_date_idx' , sqlType: "timestamp with time zone"
    }

    def beforeInsert = {
        if ( !requestDate )
        {
            requestDate = Calendar.getInstance(TimeZone.getTimeZone('GMT')).time.toTimestamp()
        }
    }

}
