package omar.wmts

class OmarWmtsLayer {
    String name
    String title
    String description
    String filter
    String sortBy

    static hasOne = [omarWmtsTileMatrixSet: OmarWmtsTileMatrixSet]

    static constraints = {
        name unique:true
        title nullable: true, blank:true
        description nullable:true, blank:true
        filter nullable:true, blank:true
        sortBy nullable:true, blank:true
    }
    static mapping = {
        name index: 'omar_wmts_layer_name_idx'
        description type: 'text'
    }
}
