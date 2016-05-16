package omar.jpip

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by gpotts on 4/1/16.
 */
@ToString(includeNames = true)
class ConvertCommand  implements Validateable
{
   String  filename
   String  entry
   String  projCode
}
