package omar.oms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 8/22/16.
 */
@ToString(includeNames = true)
class IptsToGrdCommand implements Validateable
{
   String filename
   Integer entryId = 0
   ArrayList ipts
}
