package omar.avro
import omar.core.ProcessStatus
import java.sql.Timestamp
import omar.core.DateUtil

class AvroFile {

   String processId
   String filename
   ProcessStatus status
   String statusMessage
   Date dateCreated

   static constraints = {
      processId nullable:false, unique:true
      filename nullable:false, unique:true
      status nullable:false 
      statusMessage nullable:true
      dateCreated nullable: true
   }
   static mapping = {
      cache true
      id generator: 'identity'
      processId index:"avro_file_process_id_idx"
      filename type: 'text', index: 'avro_file_filename_idx'
      status index:"avro_file_status_idx"
      statusMessage type: 'text'
      dateCreated index: 'avro_file_date_created_idx' 
   }

   def beforeInsert() {
    if ( dateCreated == null )
    {
      dateCreated = new Date()
    }

    true
  }
}
