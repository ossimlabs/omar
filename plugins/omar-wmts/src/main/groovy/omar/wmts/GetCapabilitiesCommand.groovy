package omar.wmts

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 4/17/15.
 */
@ToString(includeNames = true, includeSuper = true)
class GetCapabilitiesCommand extends WmtsCommand  implements Validateable
{
}
