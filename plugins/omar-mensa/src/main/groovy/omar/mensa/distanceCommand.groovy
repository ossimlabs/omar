package omar.mensa

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 8/22/16.
 */
@ToString(includeNames = true)
class DistanceCommand implements Validateable
{
   String filename
   Integer entryId
   String pointList

}
