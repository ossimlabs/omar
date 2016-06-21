package omar.avro

import grails.transaction.Transactional
import java.util.UUID
import omar.core.HttpStatus
import omar.core.ProcessStatus
import groovy.json.JsonSlurper

@Transactional
class AvroService {
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
    def firstObject = AvroPayload.find("FROM AvroPayload where status = 'READY' ORDER BY id asc")
    def result = firstObject?.properties

    result = result?:[:]
    firstObject?.status = "RUNNING"
    firstObject?.statusMessage = ""
    firstObject?.save(flush:true)

    result
  }
  synchronized def nextFile()
  {
    def firstObject = AvroFile.find("FROM AvroFile where status = 'READY' ORDER BY id asc")
    def result = firstObject?.properties

    result = result?:[:]
    firstObject?.status = "RUNNING"
    firstObject?.statusMessage = ""
    firstObject?.save(flush:true)

    result
  }
  File getFullPathFromMessage(String message)
  {
    JsonSlurper slurper = new JsonSlurper()
    def jsonObj = slurper.parseText(message)
    String suffix = AvroMessageUtils.getDestinationSuffixFromMessage(jsonObj)
    String prefixPath = "${OmarAvroUtils.avroConfig.download.directory}"

    new File(prefixPath, suffix)
  }
  HashMap updatePayloadStatus(String messageId, ProcessStatus status, String statusMessage)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:""
    ]

    AvroPayload avroPayload = AvroPayload.findByMessageId(messageId)

    if(avroPayload)
    {
      avroPayload.status = status
      if(statusMessage != null) avroPayload.statusMessage = statusMessage

      // for now, until we support archiving, ... etc.  once the status goes to finished
      // we will remove the file from the table.
      if(avroPayload.status == ProcessStatus.FINISHED)
      {
        avroPayload.delete(flush:true)
      }
      else
      {
        avroPayload.save(flush:true)
      }
    }
    else
    {
      result.message = "Unable to update status for id: ${messageId}"
    }
    result
  }

  Boolean isProcessingFile(String filename)
  {
    def avroFile=AvroFile.find("from AvroFile where ((filename=:filename) and (status='READY' or status='RUNNING'))",
      [filename:filename])

    avroFile != null
  }
  HashMap addFile(IndexFileCommand cmd)
  {
    HashMap result = [status:HttpStatus.OK,
                      message:"",
                      results:[],
                     ]
    try{

      String filename = cmd.filename
      AvroFile avroFile
      Boolean saveFlag = false
      avroFile = AvroFile.findByFilename(cmd.filename)
      // if it failed then we will force a reset 
      // We will delete so the ID will auto increment
      if(avroFile?.status == ProcessStatus.FAILED)
      {
        avroFile?.delete(flush:true)
        avroFile = null
      }         

      if(!avroFile)
      {
        avroFile = new AvroFile(filename: filename,
                               processId:getUniqueProcessId(),
                               status:ProcessStatus.READY,
                               statusMessage:"")
        saveFlag = true
      }
      if(saveFlag)
      {
        if(!avroFile.save(flush:true))
        {
          result.message = getAllErrors(avroFile)
          log.error "Unable to save ${cmd.filename}\n with errors: ${result.message}"
          result.remove("results") 
          result.status = HttpStatus.BAD_REQUEST 
        }
        else
        {
          log.info "Added ${avroFile.filename}"
          result.results <<
                  [
                     processId:avroFile.processId,
                     filename:avroFile.filename,
                     status:avroFile.status.name,
                     statusMessage:avroFile.statusMessage,
                     dateCreated:avroFile.dateCreated
                  ]
        }
      }
      else
      {
      // may need to log
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

  HashMap listFiles(GetFileCommand cmd)
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
                    statusMessage: record.statusMessage,
                    dateCreated: record.dateCreated,
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
    else
    {
      result.message = "Unable to update status for id: ${processId}"
    }
    result
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
    else if(cmd.whereStatusEquals)
    {
      def objects = AvroFile.findAll("FROM AvroFile where status = '${cmd.whereStatusEquals}'")

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
      File fullPathLocation = getFullPathFromMessage(cmd.message)

      if(fullPathLocation)
      {
        messageId = fullPathLocation
        if(!AvroPayload.findByMessageId(messageId))
        {
          if(!isProcessingFile(messageId))
          {
            AvroPayload avroPayload = new AvroPayload(messageId: messageId, status: ProcessStatus.READY, message:cmd.message)
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
          else
          {
            result.message = "File destination ${fullPathLocation} is already being processed and will not be added."
            log.info "File destination ${fullPathLocation} is already being processed."
          } 
        }
        else
        {
            result.message = "File destination ${fullPathLocation} is already being processed and will not be added."
            log.info "File destination ${fullPathLocation} is already being processed."
        }
      }
    }
    catch(e)
    {
      e.printStackTrace()
      result.status = HttpStatus.BAD_REQUEST 
      result.message = e.toString()
      result.remove("results")
    }

    result

  }
  HashMap listMessages(GetMessageCommand cmd)
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
                     status:record.status.toString(),
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
}