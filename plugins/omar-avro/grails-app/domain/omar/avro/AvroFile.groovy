package omar.avro
import omar.core.ProcessStatus

class AvroFile {

   String processId
   String filename
   ProcessStatus status
   String statusMessage

   static constraints = {
      processId nullable:false
      filename nullable:false
      status nullable:false 
      statusMessage nullable:true
   }
   static mapping = {
      processId index:"avro_file_process_id_idx"
      filename type: 'text', index: 'avro_file_filename_idx'
      status index:"avro_file_status_idx"
      statusMessage type: 'text'
   }
}
