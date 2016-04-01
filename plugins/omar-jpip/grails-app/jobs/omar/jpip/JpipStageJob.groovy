package omar.jpip

import omar.oms.ChipperUtil

class JpipStageJob {

   def jpipService

   static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
   }

   def execute() {
      def job = jpipService.nextJob()

      if(job)
      {
         jpipService.convertImage(job)
      }
   }
}
