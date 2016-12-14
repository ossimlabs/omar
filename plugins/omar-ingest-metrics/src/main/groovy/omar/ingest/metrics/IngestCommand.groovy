package omar.ingest.metrics

import grails.validation.Validateable
import groovy.transform.ToString

@ToString(includeNames = true)
class IngestCommand implements Validateable
{
   String ingestId
   String description
}