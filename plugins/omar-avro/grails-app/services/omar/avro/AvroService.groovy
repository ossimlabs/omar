package omar.avro

import grails.transaction.Transactional
import java.util.UUID
import omar.core.HttpStatus
import omar.core.ProcessStatus
import groovy.json.JsonSlurper

@Transactional
class AvroService {
  def ingestMetricsService

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
  def convertMessageToJsonWithSubField(String message)
  {
    JsonSlurper slurper = new JsonSlurper()
    def result

    result = slurper.parseText(message)

    if(OmarAvroUtils.avroConfig.jsonSubFieldPath)
    {
      OmarAvroUtils.avroConfig.jsonSubFieldPath.split("\\.").each{field->
        if(result."${field}" instanceof String)
        {
          result = slurper.parseText(result."${field}")
        }
        else
        {
          result = result."${field}"
        }
      }
    }

    result
  }
  File getFullPathFromMessage(String message)
  {
    File result
    if(message)
    {
      def jsonObj = convertMessageToJsonWithSubField(message)

      if(jsonObj)
      {
        String suffix = AvroMessageUtils.getDestinationSuffixFromMessage(jsonObj)
        String prefixPath = "${OmarAvroUtils.avroConfig.download.directory}"

        result = new File(prefixPath, suffix)
      }
    }

    result
  }
  HashMap updatePayloadStatus(String messageId, ProcessStatus status, String statusMessage)
  {
    HashMap result = [status:HttpStatus.SUCCESS,
                      statusCode:HttpStatus.OK,
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
        if(!avroPayload.delete(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = "Unable to delete id: ${messageId}"
        }
      }
      else
      {
        if(!avroPayload.save(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = "Unable to update id: ${messageId}"
        }
      }
    }
    else
    {
      result.status = HttpStatus.ERROR
      result.statusCode = HttpStatus.NOT_FOUND
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
    HashMap result = [status:HttpStatus.SUCCESS,
                      statusCode:HttpStatus.OK,
                      message:"",
                      data:[],
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
        if(!avroFile?.delete(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = "Unable to add file ${cmd.filename}"
        }
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
          result.data = null
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.status = HttpStatus.ERROR
        }
        else
        {
          log.info "Added ${avroFile.filename}"
          result.data <<
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
      result.statusCode = HttpStatus.BAD_REQUEST
      result.status = HttpStatus.ERROR
      result.message = e.toString()
      result.data = null
    }

    result
  }

  HashMap listFiles(GetFileCommand cmd)
  {
    HashMap result = [status:HttpStatus.SUCCESS,
            statusCode:HttpStatus.OK,
           data:[],
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
          result.data <<
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
      result.status = HttpStatus.ERROR
      result.statusCode = HttpStatus.BAD_REQUEST
      result.message = e.toString()
      result.data = null
    }

    result
  }

  HashMap updateFileStatus(String processId, ProcessStatus status, String statusMessage)
  {
    HashMap result = [
                      status: HttpStatus.SUCCESS,
                      statusCode:HttpStatus.OK,
                      message:"",
                      data:null

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
        if(!avroFile.delete(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = getAllErrors(avroFile)
        }
      }
      else
      {
        if(!avroFile.save(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = getAllErrors(avroFile)
        }
      }
    }
    else
    {
      result.status = HttpStatus.ERROR
      result.statusCode = HttpStatus.NOT_FOUND
      result.message = "Id: ${processId} not found and can't be updated"
    }
    result
  }
  HashMap resetFileProcessingCommand(ResetFileProcessingCommand cmd)
  {
    HashMap result = [statusCode:HttpStatus.OK,
                      status:HttpStatus.SUCCESS,
                      message:"",
                      data:null
                     ]
    ProcessStatus status = ProcessStatus."${cmd.status}"
    if(cmd.processId)
    {
      AvroFile avroFile = AvroFile.findByProcessId(cmd.processId)
      if(!avroFile)
      {
        result.status = HttpStatus.ERROR
        result.statusCode = HttpStatus.NOT_FOUND
        result.message = "Process ID not found: ${cmd.processId}"
      }
      else if(status == ProcessStatus.FINISHED)
      {
        if(!avroFile?.delete())
        {
          result.status     = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message    = getAllErrors(avroFile)
        }
      }
      else
      {
        avroFile?.status = status
        if(!avroFile?.save(flush:true))
        {
          result.status = HttpStatus.ERROR
          result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
          result.message = getAllErrors(avroFile)

        }
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
    HashMap result = [statusCode:HttpStatus.OK,
                      status:HttpStatus.SUCCESS,
                      message:"",
                      data:[],
                     ]
    try{
      String messageId
      File fullPathLocation = getFullPathFromMessage(cmd.message)

      if(fullPathLocation)
      {
        messageId = fullPathLocation.toString()
        ingestMetricsService.startIngest(messageId, "")
        def avroPayload = AvroPayload.findByMessageId(messageId)
        if(!avroPayload)
        {
          if(!isProcessingFile(messageId))
          {
            avroPayload = new AvroPayload(messageId: messageId, status: ProcessStatus.READY, message:cmd.message)
            if(!avroPayload.save(flush:true))
            {
              log.error "Unable to save ${cmd.message}"
              result.data
              result.message = getAllErrors(avroPayload)

              result.status = HttpStatus.ERROR
              result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            }
            else
            {
              log.info "Added message for processing with ID: ${avroPayload.messageId}"
              result.data <<
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
          if(avroPayload.status != ProcessStatus.FAILED)
          {
            result.message = "File destination ${fullPathLocation} is already being processed and will not be added."
            log.info "File destination ${fullPathLocation} is already being processed."
          }
          else
          {
            avroPayload.status = ProcessStatus.READY
            avroPayload.message = cmd.message
            if(!avroPayload.save(flush:true))
            {
              log.error "Unable to save ${cmd.message}"
              result.data = null
              result.message = getAllErrors(avroPayload)
              result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
              result.status = HttpStatus.ERROR
            }
            else
            {
              log.info "Updated message for processing with ID: ${avroPayload.messageId}"
              result.data <<
                      [
                              messageId:avroPayload.messageId,
                              message:avroPayload.message,
                      ]

              // log message with id file path
            }
          }
        }
      }
      else
      {
        result.status = HttpStatus.ERROR
        result.statusCode = HttpStatus.NOT_FOUND
        result.message = "Could not find a file to add from the message '${cmd.message}'"
        result.data = null
      }
    }
    catch(e)
    {
      e.printStackTrace()
      result.status = HttpStatus.ERROR
      result.statusCode = HttpStatus.BAD_REQUEST
      result.message = e.toString()
    }

    result

  }
  HashMap listMessages(GetMessageCommand cmd)
  {
    HashMap result = [statusCode:HttpStatus.OK,
                      status:HttpStatus.SUCCESS,
           data:[],
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
          result.data <<
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
      result.status = HttpStatus.ERROR
      result.statusCode = HttpStatus.BAD_REQUEST
      result.message = e.toString()
      result.remove("pagination")
    }

    result
  }
}