package omar.opir

class OpirRasterFile {
    String name
    String type
    Integer fileSize
    static belongsTo = [opirRasterDataSet: OpirRasterDataSet]

    static constraints = {
        name()
        type()
    }

    static mapping = {
        cache true
        id generator: 'identity'
        name index: 'opir_raster_file_name_idx'
        type index: 'opir_raster_file_type_idx'
        opirRasterDataSet index: 'opir_raster_file_raster_entry_idx'
    }
}
