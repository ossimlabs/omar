package omar.opir

class OpirRasterDataSet {
    static hasMany = [fileObjects: OpirRasterFile, rasterEntries: OpirRasterEntry ]
    Collection fileObjects
    Collection rasterEntries

    static constraints = {
    }
    static mapping = {
      cache true
      id generator: 'identity'
    }
    def getFileFromObjects(def type = "main")
    {
        return fileObjects?.find { it.type == type }
    }
}
