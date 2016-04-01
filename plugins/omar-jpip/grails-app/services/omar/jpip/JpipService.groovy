package omar.jpip

import grails.transaction.Transactional
import omar.oms.ChipperUtil

@Transactional
class JpipService {

    synchronized def nextJob(){
        def firstObject = JpipJob.first()
        def result = firstObject?.properties as HashMap
        result = result?:[:]

        firstObject?.delete()

        result
    }
    def convert(ConvertCommand cmd) {

        def row = JpipImage.findByFilenameAndEntry(cmd.filename, cmd.entry)

        if(!row)
        {
            JpipImage image = new JpipImage(filename:cmd.filename,
                                            entry:cmd.entry,
                                            jpipId:UUID.randomUUID().toString(),
                                            status:JobStatus.READY.toString())
            if(!image.save(flush:true))
            {
            }

            JpipJob job = new JpipJob(jpipId: image.jpipId,
                                      filename:cmd.filename,
                                      entry:cmd.entry)
            //image.jpipId

            if(!job.save(flush:true))
            {

            }
        }
        else
        {
             // check status
        }

    }

    def updateStatus(String jpipId, String status)
    {
        JpipImage row = JpipImage.findByJpipId(jpipId)

        if(row.status != status)
        {
            row.status = status
            row.save(flush:true)
        }

        row = null
    }
    def convertImage(HashMap jpipJobMap)
    {
        if(jpipJobMap)
        {
            def jpipId = jpipJobMap.jpipId
            HashMap initOps = [
                    hist_op:  "auto-minmax",
                    "image0.file": "${jpipJobMap?.filename}".toString(),
                    "image0.entry": "${jpipJobMap?.entry}".toString(),
                    operation:  "chip",
                    output_file:  "/tmp/${jpipJobMap?.jpipId}.jp2".toString(),
                    output_radiometry:  "U8",
                    three_band_out:  "true",
                    writer:  "ossim_kakadu_jp2",
                    writer_property0:"compression_quality=epje"]

            updateStatus(jpipId, JobStatus.RUNNING.toString())
            if(ChipperUtil.executeChipper(initOps))
            {
                updateStatus(jpipId, JobStatus.FINISHED.toString())
            }
            else
            {
                updateStatus(jpipId, JobStatus.FAILED.toString())
            }
        }
    }
}
