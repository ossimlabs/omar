package omar.predio
import io.prediction.Event;
import io.prediction.EventClient

import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional
class PredioService {

    def rate(RateCommand cmd)
    {
        HashMap result = [status:HttpStatus.OK,
                          message:"Success",
                contentType:"text/plain"
        ]

        if(cmd.validate())
        {
            try{
                PredioAppId appId = PredioAppId.findByName(cmd.appName)
                EventClient client = new EventClient(appId.accessKey, appId.url)
                if(appId)
                {
                    Event rateEvent = new Event()
                            .event(cmd.event)
                            .entityType(cmd.entityType)
                            .entityId(cmd.entityId)
                            .targetEntityType(cmd.targetEntityType)
                            .targetEntityId(cmd.targetEntityId)
                            .property("rating", cmd.rating)
                    client.createEvent(rateEvent)
                }
                else
                {
                    result.status = HttpStatus.BAD_REQUEST
                    result.message = "PredictiveIO App Instance not found for appName ${cmd.appName}"
                }
            }
            catch(e)
            {
                result.status = HttpStatus.BAD_REQUEST
                result.message = e.message
            }
        }
        else
        {
            String message
            cmd.allErrors.each{message = "${it}\n"}
            result = [status:HttpStatus.BAD_REQUEST,
                      message:message.toString()]
        }

        result
    }
}
