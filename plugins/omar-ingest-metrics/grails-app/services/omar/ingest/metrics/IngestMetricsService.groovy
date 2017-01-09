package omar.ingest.metrics

import grails.transaction.Transactional
import omar.core.ProcessStatus
import omar.core.HttpStatus
import omar.core.DateUtil
import org.hibernate.type.StandardBasicTypes
import groovy.sql.Sql

@Transactional
class IngestMetricsService
{

   def grailsApplication

   HashMap startIngest(String ingestId, String description="")
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId, description:description)

      startIngest(cmd)
   }
   HashMap startIngest(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      if (!cmd.ingestId)
      {
         result.statusCode = HttpStatus.BAD_REQUEST
         result.statusMessage = "ingestId must not be null."
         return result
      }
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)

      if (!ingestMetricsRecord)
      {
         ingestMetricsRecord = new IngestMetrics()
         ingestMetricsRecord.ingestId = cmd.ingestId
         ingestMetricsRecord.description = cmd.description
         ingestMetricsRecord.startDate = new Date()
         ingestMetricsRecord.statusMessage = cmd.statusMessage

      }
      else
      {
         ingestMetricsRecord.ingestId = cmd.ingestId
         ingestMetricsRecord.description = cmd.description
         ingestMetricsRecord.startDate = new Date()
         ingestMetricsRecord.statusMessage = cmd.statusMessage
         ingestMetricsRecord.endDate = null
         ingestMetricsRecord.startCopy = null
         ingestMetricsRecord.endCopy = null
         ingestMetricsRecord.startStaging = null
         ingestMetricsRecord.endStaging = null
      }
      ingestMetricsRecord?.status = ProcessStatus.RUNNING.toString()

      if (!ingestMetricsRecord?.save(flush: true))
      {
         result.statusCode = HttpStatus.BAD_REQUEST
         result.statusMessage = "Unable to save ${cmd.ingestId}"
      } else
      {
         HashMap record = ingestMetricsRecord.properties
         result.data << record
      }

      result
   }

   HashMap endIngest(String ingestId)
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId)

      endIngest(cmd)
   }
   HashMap endIngest(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)

      if (!ingestMetricsRecord)
      {
         result.statusCode = HttpStatus.NOT_FOUND
         result.statusMessage = "unable to find Record with id ${cmd.ingestId}"
      } else
      {
         ingestMetricsRecord.endDate = new Date()
         ingestMetricsRecord.status = ProcessStatus.FINISHED.toString()
         if (!ingestMetricsRecord?.save(flush: true))
         {
            result.statusCode = HttpStatus.BAD_REQUEST
            result.statusMessage = "Unable to save ${cmd.ingestId}"
         } else
         {
            HashMap record = ingestMetricsRecord.properties
            result.data << record
         }
      }

      result
   }

   HashMap startCopy(String ingestId)
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId)

      startCopy(cmd)
   }

   HashMap startCopy(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)
      Date startCopyDate = new Date()
      // we will default a startDate to the same date
      if (!ingestMetricsRecord)
      {
         ingestMetricsRecord = new IngestMetrics()
         ingestMetricsRecord.ingestId = cmd.ingestId
         ingestMetricsRecord.startDate = startCopyDate
        // result.statusCode = HttpStatus.NOT_FOUND
        // result.statusMessage = "unable to find Record with id ${cmd.ingestId}"
      }
      ingestMetricsRecord.startCopy = startCopyDate
      if (!ingestMetricsRecord?.save(flush: true))
      {
         result.statusCode = HttpStatus.BAD_REQUEST
         result.statusMessage = "Unable to save ${cmd.ingestId}"
      } else
      {
         HashMap record = ingestMetricsRecord.properties
         result.data << record
      }

      result
   }

   HashMap endCopy(String ingestId)
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId)

      endCopy(cmd)
   }
   HashMap endCopy(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)

      if (!ingestMetricsRecord)
      {
         result.statusCode = HttpStatus.NOT_FOUND
         result.statusMessage = "unable to find Record with id ${cmd.ingestId}"
      }
      else
      {
         ingestMetricsRecord.endCopy = new Date()
         if (!ingestMetricsRecord?.save(flush: true))
         {
            result.statusCode = HttpStatus.BAD_REQUEST
            result.statusMessage = "Unable to save ${cmd.ingestId}"
         } else
         {
            HashMap record = ingestMetricsRecord.properties
            result.data << record
         }
      }

      result
   }

   HashMap startStaging(String ingestId)
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId)

      startStaging(cmd)
   }
   HashMap startStaging(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)
      Date startStagingDate = new Date();
      if (!ingestMetricsRecord)
      {
         ingestMetricsRecord = new IngestMetrics()
         ingestMetricsRecord.ingestId = cmd.ingestId
         ingestMetricsRecord.startDate = startStagingDate
//         result.statusCode = HttpStatus.NOT_FOUND
//         result.statusMessage = "unable to find Record with id ${cmd.ingestId}"
      }
      ingestMetricsRecord.startStaging = startStagingDate
      if (!ingestMetricsRecord?.save(flush: true))
      {
         result.statusCode = HttpStatus.BAD_REQUEST
         result.statusMessage = "Unable to save ${cmd.ingestId}"
      } else
      {
         HashMap record = ingestMetricsRecord.properties
         result.data << record
      }

      result
   }

   HashMap endStaging(String ingestId)
   {
      IngestCommand cmd = new IngestCommand(ingestId: ingestId)

      endStaging(cmd)
   }
   HashMap endStaging(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)

      if (!ingestMetricsRecord)
      {
         result.statusCode = HttpStatus.NOT_FOUND
         result.statusMessage = "unable to find Record with id ${cmd.ingestId}"
      } else
      {
         ingestMetricsRecord.endStaging = new Date()
         if (!ingestMetricsRecord?.save(flush: true))
         {
            result.statusCode = HttpStatus.BAD_REQUEST
            result.statusMessage = "Unable to save ${cmd.ingestId}"
         } else
         {
            HashMap record = ingestMetricsRecord.properties
            result.data << record
         }
      }

      result
   }

   HashMap save(IngestCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : [],
//              pagination: [
//                      count: 0,
//                      offset: 0,
//                      limit: 0
//              ]
      ]
      def ingestMetricsRecord = getIngestMetrics(cmd.ingestId)
      Boolean changedFlag = false
      if(!ingestMetricsRecord&&(cmd.ingestId||cmd.newIngestId))
      {
         ingestMetricsRecord = new IngestMetrics()
         if(cmd.newIngestId)
         {
            ingestMetricsRecord.ingestId = cmd.newIngestId
         }
         else
         {
            ingestMetricsRecord.ingestId = cmd.ingestId
         }
         changedFlag = true
      }
      if (ingestMetricsRecord)
      {
         if (cmd.description)
         {
            changedFlag = true
            ingestMetricsRecord.description = cmd.description ?: ingestMetricsRecord.description
         }
         if(cmd.newIngestId)
         {
            def newIngestMetricsRecord = getIngestMetrics(cmd.newIngestId)

            if(!newIngestMetricsRecord)
            {
               changedFlag =   ingestMetricsRecord.ingestId != cmd.newIngestId
               ingestMetricsRecord.ingestId = cmd.newIngestId
            }
            else
            {
               result.statusCode = HttpStatus.BAD_REQUEST
               result.statusMessage = "the new ingestId already exists in the database so can't rename the record with ${ingestId} to ${newIngestId}"

               return result;
            }
         }
         if (cmd.startDate)
         {
            changedFlag = true
            ingestMetricsRecord.startDate = DateUtil.dateTimeToDate(cmd.startDate)
         }
         if (cmd.endDate)
         {
            changedFlag = true
            ingestMetricsRecord.endDate = DateUtil.dateTimeToDate(cmd.endDate)
         }
         if (cmd.startCopy)
         {
            changedFlag = true
            ingestMetricsRecord.startCopy = DateUtil.dateTimeToDate(cmd.startCopy)
         }
         if (cmd.endCopy)
         {
            changedFlag = true
            ingestMetricsRecord.endCopy = DateUtil.dateTimeToDate(cmd.endCopy)
         }
         if (cmd.startStaging)
         {
            changedFlag = true
            ingestMetricsRecord.startStaging = DateUtil.dateTimeToDate(cmd.startStaging)
         }
         if (cmd.endStaging)
         {
            changedFlag = true
            ingestMetricsRecord.endStaging = DateUtil.dateTimeToDate(cmd.endStaging)
         }
         if (cmd.status)
         {
            changedFlag = (ingestMetricsRecord.status!=cmd.status)
            ingestMetricsRecord.status = cmd.status
         }
         if(cmd.statusMessage != null)
         {
            changedFlag = (ingestMetricsRecord.statusMessage!=cmd.statusMessage)
            ingestMetricsRecord.statusMessage = cmd.statusMessage
         }
      }
      if (changedFlag)
      {
         if (!ingestMetricsRecord?.save(flush: true))
         {
            result.statusCode = HttpStatus.BAD_REQUEST
            result.statusMessage = "Unable to save ${ingestMetricsRecord.ingestId}"
         }
         else
         {
            HashMap record = ingestMetricsRecord.properties
            result.data << record
            result.statusMessage = "Updated record ${ingestMetricsRecord.ingestId}"
         }
      }
      else
      {
         result.statusMessage = "No updates required"
      }

      result
   }

   HashMap delete(DeleteCommand cmd)
   {
      HashMap result = [
              statusCode   : HttpStatus.OK,
              statusMessage: ""
      ]
      String deleteStatement = "DELETE IngestMetrics "

      try{
         if(cmd.ingestId)
         {
            deleteStatement = "${deleteStatement} WHERE ingestId='${cmd.ingestId}'"
         }
         else if (cmd.startDate && cmd.endDate)
         {
            deleteStatement = "${deleteStatement} WHERE startDate >= '${cmd.startDate}' AND endDate <= '${cmd.endDate}'"
         }
         else if (cmd.startDate)
         {
            deleteStatement = "${deleteStatement} WHERE startDate >= '${cmd.startDate}'"
         }
         else if (cmd.endDate)
         {
            deleteStatement = "${deleteStatement} WHERE endDate <='${cmd.endDate}'"
         }

         IngestMetrics.executeUpdate(deleteStatement.toString());
      }
      catch(e)
      {
         result.statusCode = HttpStatus.BAD_REQUEST
         result.statusMessage = "Error deleting records: ${e}".toString()
      }

      result
   }

   def ingestIdSummary(SummaryCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : [],
              pagination: [
                      count : 0,
                      offset: 0,
                      limit : 0
              ]
      ]
      def baseCriteria = {

         if(cmd.ingestId)
         {
            eq("ingestId", cmd.ingestId)
         }
         else if (cmd.startDate && cmd.endDate)
         {
            and {
               ge("startDate",  DateUtil.dateTimeToDate(cmd.startData))
               le("endDate",  DateUtil.dateTimeToDate(cmd.endData))
            }
         }
         else if (cmd.startDate)
         {
            ge('startDate',  DateUtil.dateTimeToDate(cmd.startDate))
         }
         else if (cmd.endDate)
         {
            le('endDate',  DateUtil.dateTimeToDate(cmd.endDate))
         }
      }

      // need to externalize
      if(cmd.offset == null) cmd.offset = 0;
      if(cmd.limit == null) cmd.limit = 10000;
      if(cmd.limit > 10000) cmd.limit = 10000;

      Integer count = IngestMetrics.createCriteria().count{
         baseCriteria.delegate = delegate
         baseCriteria()
      }

      def records = IngestMetrics.createCriteria().list(max:cmd.limit, offset:cmd.offset){
         baseCriteria.delegate = delegate
         baseCriteria()
      }

      result.pagination.count  = count
      result.pagination.offset = cmd.offset
      result.pagination.limit  = cmd.limit

      records.each{record->
         Double totalTime
         Double totalCopyTime
         Double totalStagingTime

         Double totalOverheadTime
         Boolean totalOverheadTimeValid = true;
         if(record.endDate&&record.startDate)
         {
            totalTime = (record.endDate.time - record.startDate.time) / 1000.0
            totalOverheadTime = totalTime
         }
         else
         {
            totalOverheadTimeValid = false
         }
         if(record.endCopy&&record.startCopy)
         {
            totalCopyTime = (record.endCopy.time - record.startCopy.time) / 1000.0
            if(totalOverheadTimeValid) totalOverheadTime -= totalCopyTime
         }
         else
         {
            totalOverheadTimeValid = false
         }
         if(record.startStaging&&record.endStaging)
         {
            totalStagingTime =  (record.endStaging.time-record.startStaging.time)/1000.0
            if(totalOverheadTimeValid) totalOverheadTime -= totalStagingTime
         }
         else
         {
            totalOverheadTimeValid = false
         }
         HashMap tempRecord = [ ingestId:         record.ingestId,
                                description:      record.description,
                                totalTime:        totalTime,
                                totalCopyTime:    totalCopyTime,
                                totalStagingTime: totalStagingTime,
                                status:           record.status
         ]
         if(totalOverheadTimeValid)
         {
            tempRecord.totalOverheadTime = totalOverheadTime
         }
         else
         {
            tempRecord.totalOverheadTime = null
         }

         result.data << tempRecord

      }

      result
   }

   def fullSummary(SummaryCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : [],
              pagination: [
                      count : 0,
                      offset: 0,
                      limit : 0
              ]
      ]
      Integer itemCount
      Double averageTime;
      Double totalTime;
      Double averageCopyTime;
      Double totalCopyTime;
      Double averageStagingTime;
      Double totalStagingTime;
      Double totalOverheadTime;
      Double averageOverheadTime;
      Boolean totalOverheadTimeValid = true;
      Boolean averageOverheadTimeValid = true;
      def baseCriteria = {
         eq("status", "FINISHED")

         if (cmd.startDate && cmd.endDate)
         {
            and {
               ge("startDate",  DateUtil.dateTimeToDate(cmd.startData))
               le("endDate",  DateUtil.dateTimeToDate(cmd.endData))
            }
         }
         else if (cmd.startDate)
         {
            ge('startDate',  DateUtil.dateTimeToDate(cmd.startDate))
         } else if (cmd.endDate)
         {
            le('endDate',  DateUtil.dateTimeToDate(cmd.endDate))
         }
      }


      def criteriaResult = IngestMetrics.createCriteria().list {
         baseCriteria.delegate=delegate
         baseCriteria()
         projections {
            sqlProjection("SUM(ABS(extract(DAYS from end_date-start_date))*86400+ABS(extract(HOURS from end_date-start_date))*3600+ABS(extract(MINUTES from end_date-start_date))*60+ABS(extract(SECONDS from end_date-start_date))) as total",
                    "total",
                    StandardBasicTypes.DOUBLE)
            sqlProjection("AVG(ABS(extract(DAYS from end_date-start_date))*86400+ABS(extract(HOURS from end_date-start_date))*3600+ABS(extract(MINUTES from end_date-start_date))*60+ABS(extract(SECONDS from end_date-start_date))) as average",
                    "average",
                    StandardBasicTypes.DOUBLE)
         }
         and {
            isNotNull('startDate')
            isNotNull('endDate')
            eq("status", "FINISHED")
         }
      }[0]

      if (criteriaResult)
      {
         totalTime = criteriaResult[0]
         averageTime = criteriaResult[1]

         if(totalTime == null)
         {
            totalOverheadTimeValid = false;
         }
         if(averageTime == null)
         {
            averageOverheadTimeValid = false;
         }
         totalOverheadTime = totalTime
         averageOverheadTime = averageTime
      }
      else
      {
         totalOverheadTimeValid = false;
         averageOverheadTimeValid = false;
      }
      criteriaResult = IngestMetrics.createCriteria().list {
         baseCriteria.delegate=delegate
         baseCriteria()
         projections {
            sqlProjection("SUM(ABS(extract(DAYS from end_copy-start_copy))*86400+ABS(extract(HOURS from end_copy-start_copy))*3600+ABS(extract(MINUTES from end_copy-start_copy))*60+ABS(extract(SECONDS from end_copy-start_copy))) as total",
                    "total",
                    StandardBasicTypes.DOUBLE)
            sqlProjection("AVG(ABS(extract(DAYS from end_copy-start_copy))*86400+ABS(extract(HOURS from end_copy-start_copy))*3600+ABS(extract(MINUTES from end_copy-start_copy))*60+ABS(extract(SECONDS from end_copy-start_copy))) as average",
                    "average",
                    StandardBasicTypes.DOUBLE)
         }
         and {
            isNotNull('startCopy')
            isNotNull('endCopy')
            eq("status", "FINISHED")
         }
      }[0]
      if (criteriaResult)
      {
         totalCopyTime = criteriaResult[0]
         averageCopyTime = criteriaResult[1]
         if(totalCopyTime == null)
         {
            totalOverheadTimeValid = false
         }
         if(averageCopyTime == null)
         {
            averageOverheadTimeValid = false
         }
         if(totalOverheadTimeValid) totalOverheadTime -= totalCopyTime

         if(averageOverheadTimeValid) averageOverheadTime -= averageCopyTime
      }
      else
      {
         totalOverheadTimeValid = false;
         averageOverheadTimeValid = false;
      }

      criteriaResult = IngestMetrics.createCriteria().list {
         baseCriteria.delegate=delegate
         baseCriteria()
         projections {
            sqlProjection("SUM(ABS(extract(DAYS from end_staging-start_staging))*86400+ABS(extract(HOURS from end_staging-start_staging))*3600+ABS(extract(MINUTES from end_staging-start_staging))*60+ABS(extract(SECONDS from end_staging-start_staging))) as total",
                    "total",
                    StandardBasicTypes.DOUBLE)
            sqlProjection("AVG(ABS(extract(DAYS from end_staging-start_staging))*86400+ABS(extract(HOURS from end_staging-start_staging))*3600+ABS(extract(MINUTES from end_staging-start_staging))*60+ABS(extract(SECONDS from end_staging-start_staging))) as average",
                    "average",
                    StandardBasicTypes.DOUBLE)
         }
         and {
            isNotNull('startStaging')
            isNotNull('endStaging')
            eq("status", "FINISHED")
         }
      }[0]
      if (criteriaResult)
      {
         totalStagingTime = criteriaResult[0]
         averageStagingTime = criteriaResult[1]
         if(totalStagingTime == null)
         {
            totalOverheadTimeValid = false
         }
         if(averageStagingTime == null)
         {
            averageOverheadTimeValid = false
         }
         if(totalOverheadTimeValid) totalOverheadTime -= totalStagingTime
         if(averageOverheadTimeValid) averageOverheadTime -= averageStagingTime
      }
      else
      {
         totalOverheadTimeValid = false;
         averageOverheadTimeValid = false;
      }

      itemCount = IngestMetrics.createCriteria().count{
         baseCriteria.delegate=delegate
         baseCriteria()
      }
       if(totalOverheadTimeValid)
       {
          if(totalOverheadTime<0) totalOverheadTimeValid = false
       }
      if(averageOverheadTimeValid)
      {
         if(averageOverheadTime<0) averageOverheadTimeValid = false
      }
      if(!totalOverheadTimeValid||!averageOverheadTimeValid)
      {
         totalOverheadTimeValid =  averageOverheadTimeValid = false
      }
      if(itemCount)
      {
         HashMap tempRecord = [
                 itemCount         : itemCount,
                 totalTime         : totalTime,
                 totalCopyTime     : totalCopyTime,
                 totalStagingTime  : totalStagingTime,
                 averageTime       : averageTime,
                 averageCopyTime   : averageCopyTime,
                 averageStagingTime: averageStagingTime,
         ]
         if(totalOverheadTimeValid)
         {
            tempRecord.totalOverheadTime = totalOverheadTime
         }
         else
         {
            tempRecord.totalOverheadTime = null
         }
         if(averageOverheadTimeValid)
         {
            tempRecord.averageOverheadTime = averageOverheadTime
         }
         else
         {
            tempRecord.averageOverheadTime = null
         }
         result.data << tempRecord
      }
      else
      {
//         result.statusCode = HttpStatus.NOT_FOUND
         result.statusMessage = "No data matches the constraints"
      }

      result.remove("pagination")

      result
   }

   def summary(SummaryCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : [],
              pagination: [
                      count : 0,
                      offset: 0,
                      limit : 0
              ]
      ]
      if(cmd.ingestId||cmd.individual)
      {
         result = ingestIdSummary(cmd)
      }
      else if(cmd.startDate||cmd.endDate)
      {
         result = fullSummary(cmd)
      }
      else
      {
         result = fullSummary(cmd)
      }

      result
   }
   def setStatus(String ingestId, String status, String statusMessage="")
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              statusMessage: "",
              data      : []
      ]
      def ingestMetricsRecord = getIngestMetrics(ingestId)
      if(ingestMetricsRecord)
      {
         ingestMetricsRecord.status        = status
         ingestMetricsRecord.statusMessage = statusMessage

         result.data << ingestMetricsRecord.properties

         if(!ingestMetricsRecord.save(flush:true))
         {
            result.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            result.statusMessage = "Unable to set the status for ${ingestId}".toString()
         }
      }
      else
      {
         result.statusCode = HttpStatus.NOT_FOUND
         result.statusMessage = "ID ${ingestId} not found"
      }

      result
   }

   def list(ListCommand cmd)
   {
      HashMap result = [
              statusCode: HttpStatus.OK,
              data      : [],
              pagination: [
                      count : 0,
                      offset: 0,
                      limit : 0
              ]
      ]
      def baseCriteria = {
         if(cmd.ingestId)
         {
            eq("ingestId", cmd.ingestId)
         }
         if (cmd.startDate && cmd.endDate)
         {
            and {
               ge("startDate",  DateUtil.dateTimeToDate(cmd.startData))
               le("endDate",  DateUtil.dateTimeToDate(cmd.endData))
            }
         }
         else if (cmd.startDate)
         {
            gt('startDate',  DateUtil.dateTimeToDate(cmd.startDate))
         } else if (cmd.endDate)
         {
            lt('endDate',  DateUtil.dateTimeToDate(cmd.endDate))
         }
      }

      // need to externalize
      if(cmd.offset == null) cmd.offset = 0;
      if(cmd.limit == null) cmd.limit = 10000;
      if(cmd.limit > 10000) cmd.limit = 10000;

      Integer count = IngestMetrics.createCriteria().count{
         baseCriteria.delegate = delegate
         baseCriteria()
      }

      def records = IngestMetrics.createCriteria().list(max:cmd.limit, offset:cmd.offset, sort:cmd.sortBy, order:cmd.order){
         baseCriteria.delegate = delegate
         baseCriteria()
      }

      result.pagination.count  = count
      result.pagination.offset = cmd.offset
      result.pagination.limit  = cmd.limit
      records.each{record->
         result.data << record.properties
      }

      result
   }

   def getIngestMetrics(String ingestId)
   {
      IngestMetrics.findByIngestId(ingestId)
   }
}
