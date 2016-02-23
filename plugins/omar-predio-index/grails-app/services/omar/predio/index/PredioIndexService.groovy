package omar.predio.index

import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional
class PredioIndexService {

    def indexData(PredioIndexDataCommand cmd) {
        HashMap result = [status     : HttpStatus.OK,
                          message    : "Success",
                          contentType: "text/plain"]
        String dateRanges = cmd.dateRanges
        String locationFields = cmd.locationFields
        String categoryFields = cmd.categoryFields
        String dateField      = cmd.dateField
        String expirePeriod   = cmd.expirePeriod
        def config = OmarPredioIndexUtils.predioIndexConfig

        //default if not present
        if(!cmd.wfsUrl){
            def wfsParams = config.wfs.params.inject([]){resultList,k,v-> resultList<<"${k}=${v}"}.join("&")
            cmd.wfsUrl = "${config.wfs.baseUrl}?${wfsParams}"
        }
        if(!cmd.locationFields)
        {
            cmd.locationFields = config.fields.locations.join(",")
        }
        if(!cmd.categoryFields)
        {
            cmd.categoryFields = config.fields.categories.join(",")
        }
        if(!cmd.dateField)
        {
            cmd.dateField = config.dateField
        }
        if(!cmd.idField)
        {
            cmd.idField = config.idField
        }

        try{
            def indexJobRecord = new PredioIndexJob(cmd.properties)
            indexJobRecord.save(flush:true)
        }
        catch(e)
        {
            result = [status : HttpStatus.BAD_REQUEST,
                      message: message.toString()]
        }
//        def timezone = TimeZone.getTimeZone("UTC")
//        if(cmd.dateRanges)
//        {
//            def dateRanges = DateUtil.parseOgcTimeIntervalsAsDateTime(cmd.dateRanges)//, DateTimeZone.UTC
//            println dateRanges
//
//            def expirePeriod = DateUtil.parsePeriod(cmd.expirePeriod)
//            println expirePeriod
//        }

        result

    }
}
