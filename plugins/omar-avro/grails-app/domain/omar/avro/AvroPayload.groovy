package omar.avro

class AvroPayload {
  String messageId
  String message
  Date dateCreated
  static constraints = {
    messageId   nullable: false, unique:true 
    message     nullable: false
    dateCreated nullable: true
  }
  static mapping = {
    messageId type:'text', index: 'omar_avro_payload_message_id_idx'
    message type: 'text'
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
