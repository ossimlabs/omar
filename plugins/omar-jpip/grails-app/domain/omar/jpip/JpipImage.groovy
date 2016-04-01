package omar.jpip

import java.sql.Timestamp

class JpipImage {
    String    jpipId
    String    filename
    String    entry
    Timestamp requestDate
    Timestamp createDate
    String    status

    static constraints = {
        jpipId (nullable:false)
        filename (nullable:false)
        entry (nullable:false)
        requestDate( nullable: true )
        createDate( nullable: true )
        status( nullable: false )
    }

    static mapping = {
        jpipId      index: 'jpip_image_jpip_id_idx'
        filename    index: 'jpip_image_filename_idx'
        entry    index: 'jpip_image_entry_idx'
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
