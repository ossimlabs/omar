package omar.stager
import omar.core.ProcessStatus

class StageFileJob {
   def stagerService

   static triggers = {
      simple name: 'StageFileJobTrigger', group: 'StageFileJobGroup', repeatInterval: 5000l
   }

   def execute() {
      log.trace "Entered........."
      def fileRecord
      while(fileRecord = stagerService.nextFileToStage())
      {
         try
         {
            def result = stagerService.stageFileJni([filename:fileRecord.filename,
                                        buildOverviews: fileRecord.buildOverviews,
                                        buildHistograms:fileRecord.buildHistograms,
                                        overviewCompressionType: fileRecord.overviewCompressionType,
                                        overviewType: fileRecord.overviewType
            ])
            if(result.status>=300)
            {
               if(result.status == 415)
               {
                  log.error  result?.message?:"File ${fileRecord.filename} not added.  We currently do not support updating."
                  stagerService.updateFileStatus(fileRecord.processId, ProcessStatus.FINISHED, result?.message?:"Failed to stage file ${fileRecord.filename}")
               }
               else
               {
                  log.error  result?.message?:"Failed to stage file ${fileRecord.filename}"
                  stagerService.updateFileStatus(fileRecord.processId, ProcessStatus.FAILED, result?.message?:"Failed to stage file ${fileRecord.filename}")
               }
            }
            else
            {
               stagerService.updateFileStatus(fileRecord.processId, ProcessStatus.FINISHED, "File ${fileRecord.filename} successfully staged")
            }
         }
         catch(e)
         {
            log.error e.toString()
            stagerService.updateFileStatus(fileRecord.processId, ProcessStatus.FAILED, "File ${fileRecord.filename} NOT staged with error ${e}")
         }
      }
      log.trace "Leaving..........."
   }
}
