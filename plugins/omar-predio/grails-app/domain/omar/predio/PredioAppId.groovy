package omar.predio

import groovy.transform.ToString

@ToString(includeNames = true)
class PredioAppId
{
    String url
    String name
    String accessKey
    static constraints = {
        name       unique: true, blank: false
        url        unique:false, blank:false
        accessKey  blank: false
    }
}
