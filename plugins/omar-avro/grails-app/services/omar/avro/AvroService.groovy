package omar.avro

import grails.transaction.Transactional
import java.util.UUID
import omar.core.HttpStatus
import omar.core.ProcessStatus

@Transactional
class AvroService {
  private getUniqueMessageId()
  {
    String result = UUID.randomUUID().toString()

    while(AvroPayload.findByMessageId(result))
    {
       result = UUID.randomUUID().toString()
    }

    result
  }
  private getUniqueProcessId()
  {
    String result = UUID.randomUUID().toString()

    while(AvroFile.findByProcessId(result))
    {
       result = UUID.randomUUID().toString()
    }

    result
  }
  private String getAllErrors(def domainObject)
  {
    String result
    domainObject?.errors?.allErrors?.each{err->
            if (result) result = "${result}\n${err}"
            else result = err
    }
    result
  }
  synchronized def nextMessage()
  {
    def firstObject = AvroPayload.first()
    def result = firstObject?.properties
    result = result?:[:]

    firstObject?.delete(flush:true)

    result
  }
  synchronized def nextFile()
  {
//    def firstObject = AvroFile.first()
    def firstObject = AvroFile.find("FROM AvroFile where status = 'READY' ORDER BY id asc")
    def result = firstObject?.properties
    result = result?:[:]
    firstObject?.status = "RUNNING"
    firstObject?.statusMessage = ""
    firstObject?.save(flush:true)
//    firstObject?.delete(flush:true)

    result
  }

  HashMap addFile(IndexFileCommand cmd)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:"",
                      results:[],
                     ]
    try{
       String filename = cmd.filename
       AvroFile avroFile = new AvroFile(filename: filename,
        processId:getUniqueProcessId(),
        status:ProcessStatus.READY,
        statusMessage:"")

       if(!avroFile.save(flush:true))
       {
          result.message = getAllErrors(avroFile)
          log.error "Unable to save ${cmd.filename}\n with errors: ${result.message}"
          result.remove("results")  
       }
       else
       {
          log.info "Added ${avroFile.filename}"
          result.results <<
                  [
                     filename:avroFile.filename,
                  ]
       }
    }
    catch(e)
    {
      result.status = HttpStatus.BAD_REQUEST 
      result.message = e.toString()
      result.remove("results")
    }

    result

  }

  HashMap listFile(GetFileCommand cmd)
  {
    HashMap result = [
           results:[],
           pagination: [
                   count: 0,
                   offset: 0,
                   limit: 0
           ]
    ]

    try 
    {
      result.pagination.count = AvroFile.count()
      result.pagination.offset = cmd.offset?:0
      Integer limit = cmd.limit?:result.pagination.count

      AvroFile.list([offset:result.pagination.offset, max:limit]).each{record->
          result.results <<
                  [
                    filename:record.filename,
                    processId: record.processId,
                    status: record.status.name,
                    statusMessage: record.statusMessage
                  ]
      }

      result.pagination.limit = limit           
    }
    catch(e) 
    {
      result.status = HttpStatus.BAD_REQUEST 
      result.message = e.toString()
      result.remove("results")
      result.remove("pagination")
    }


    result
  }
  HashMap updateFileStatus(String processId, ProcessStatus status, String statusMessage)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:""
                     ]

    AvroFile avroFile = AvroFile.findByProcessId(processId)

    if(avroFile)
    {
      avroFile.status = status
      if(statusMessage != null) avroFile.statusMessage = statusMessage

      // for now, until we support archiving, ... etc.  once the status goes to finished
      // we will remove the file from the table.
      if(avroFile.status == ProcessStatus.FINISHED)
      {
        avroFile.delete(flush:true)
      }
      else
      {
        avroFile.save(flush:true)
      }
    }

  }
  HashMap resetFileProcessingCommand(ResetFileProcessingCommand cmd)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:""
                     ]
    ProcessStatus status = ProcessStatus."${cmd.status}"
    if(cmd.processId)
    {
      AvroFile avroFile = AvroFile.findByProcessId(processId)
      if(status == ProcessStatus.FINISHED)
      {
        avroFile?.delete()
      }
      else
      {
        avroFile?.status = status
        avroFile?.save(flush:true)

      }
      if(!avroFile)
      {
        result.status = HttpStatus.NOT_FOUND
        result.message = "Process ID not found: ${cmd.processId}"
      }
    }
    else if(cmd.whereStatus)
    {
      def objects = AvroFile.findAll("FROM AvroFile where status = '${cmd.whereStatus}'")

      objects.each{record->
        record.status = status
        if(status == ProcessStatus.FINISHED)
        {
          record.delete()
        }
        else
        {
          record.save()
        }
      }
      AvroFile.withSession{session->
        session.flush()
      }
    }
    else
    {
      AvroFile.list().each{record->
        if(status == ProcessStatus.FINISHED)
        {
          record.delete()
        }
        else
        {
          record.save()
        }
      }
      AvroFile.withSession{session->
        session.flush()
      }
    }

    result
  }
  HashMap addMessage(IndexMessageCommand cmd)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:"",
                      results:[],
                     ]
    try{
       String messageId
       if(!cmd.messageId) messageId = getUniqueMessageId()
       AvroPayload avroPayload = new AvroPayload(messageId: messageId, message:cmd.message)
       if(!avroPayload.save(flush:true))
       {
          log.error "Unable to save ${cmd.message}"
          result.remove("results")
          result.message = getAllErrors(avroPayload)

          result.status = HttpStatus.BAD_REQUEST 
       }
       else
       {
          log.info "Added message for processing with ID: ${avroPayload.messageId}"
          result.results <<
                  [
                     messageId:avroPayload.messageId,
                     message:avroPayload.message,
                  ]
       }
    }
    catch(e)
    {
       result.status = HttpStatus.BAD_REQUEST 
       result.message = e.toString()
       result.remove("results")
    }

    result

  }
  HashMap listMessage(GetMessageCommand cmd)
  {
    HashMap result = [
           results:[],
           pagination: [
                   count: 0,
                   offset: 0,
                   limit: 0
           ]
    ]
    try 
    {
      result.pagination.count = AvroPayload.count()
      result.pagination.offset = cmd.offset?:0
      Integer limit = cmd.limit?:result.pagination.count

      AvroPayload.list([offset:result.pagination.offset, max:limit]).each{record->
          result.results <<
                  [
                     messageId:record.messageId,
                     message:record.message,
                  ]
      }

      result.pagination.limit = limit           
    }
    catch(e) 
    {
      result.status = HttpStatus.BAD_REQUEST 
      result.message = e.toString()
      result.remove("results")
      result.remove("pagination")
    }

    result
  }
}
