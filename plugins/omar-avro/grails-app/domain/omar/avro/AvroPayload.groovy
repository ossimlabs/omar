package omar.avro
import omar.core.ProcessStatus

class AvroPayload {
  String messageId
  String message
  ProcessStatus status
  String statusMessage
  Date dateCreated
  static constraints = {
    messageId   nullable: false, unique:true 
    message     nullable: false
    status      nullable:false
    statusMessage nullable: true
    dateCreated nullable: true
  }
  static mapping = {
    cache true
    id generator: 'identity'
    messageId type:'text', index: 'avro_payload_message_id_idx'
    message type: 'text'
    statusMessage type: 'text'
    dateCreated index: 'avro_payload_date_created_idx'
  }
  def beforeInsert() {
    if ( dateCreated == null )
    {
      dateCreated = new Date()
    }
    if(status == null) status = ProcessStatus.READY
    true
  }
}
