package omar.avro

class AvroPayload {
   String messageId
   String message
   static constraints = {
      messageId nullable:false 
      message nullable:false
   }
  static mapping = {
    messageId index: 'omar_avro_payload_message_id_idx'
    message type: 'text'
  }
}
