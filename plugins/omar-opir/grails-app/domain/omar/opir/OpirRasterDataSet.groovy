package omar.opir

class OpirRasterDataSet {
    String filename
    static hasMany = [rasterEntries: OpirRasterEntry]

    static constraints = {
        filename( unique: true, nullable:false )

    }
    static mapping = {
        opirRasterEntry index: 'opir_raster_data_set_filename_idx'
    }
}
