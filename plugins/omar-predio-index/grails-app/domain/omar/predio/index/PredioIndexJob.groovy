package omar.predio.index

class PredioIndexJob {
    String wfsUrl
    static constraints = {
        wfsUrl blank: false, unique: false
    }
}
