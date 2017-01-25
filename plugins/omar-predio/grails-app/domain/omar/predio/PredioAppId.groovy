package omar.predio

import groovy.transform.ToString

@ToString(includeNames = true)
class PredioAppId
{
    String eventUrl
    String queryUrl
    String name
    String accessKey

    static mapping = {
      cache true
      id generator: 'identity'
    }

    static constraints = {
        name       unique:   true,  blank: false
        eventUrl   unique:   false, nullable: true
        queryUrl   unique:   false, nullable: true
        accessKey  nullable: true
    }
}
