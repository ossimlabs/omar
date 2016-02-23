package omar.predio.index

import org.joda.time.DateTime
class PredioIndexJob {
    String wfsUrl
    String dateRanges
    String locationFields
    String categoryFields
    String dateField
    String idField
    String expirePeriod

    static constraints = {
        wfsUrl nullable: false, blank: false
        dateRanges     nullable:true
        locationFields nullable:false, blank:false
        categoryFields nullable:false, blank:false
        dateField      nullable:false, blank:false
        idField      nullable:false, blank:false
        expirePeriod   nullable:true, blank:true
    }
    static mapping = {
        //url sqlType: "timestamp with time zone"
    }
}
