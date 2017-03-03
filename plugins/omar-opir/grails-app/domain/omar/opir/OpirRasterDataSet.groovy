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


    static OpirRasterDataSet initRasterDataSet(rasterDataSetNode, rasterDataSet = null)
    {
        rasterDataSet = rasterDataSet ?: new OpirRasterDataSet()

        for ( def rasterFileNode in rasterDataSetNode.fileObjects.RasterFile )
        {
            OpirRasterFile rasterFile = OpirRasterFile.initRasterFile( rasterFileNode )
            rasterDataSet.addToFileObjects( rasterFile )
        }

        for ( def rasterEntryNode in rasterDataSetNode.rasterEntries.RasterEntry )
        {
            OpirRasterEntry rasterEntry = new OpirRasterEntry()
            rasterEntry.rasterDataSet = rasterDataSet
            OpirRasterEntry.initRasterEntry( rasterEntryNode, rasterEntry )

            if ( rasterEntry.groundGeom )
            {
                rasterDataSet.addToRasterEntries( rasterEntry )
            }
        }

        return rasterDataSet
    }

    def getFileFromObjects(def type = "main")
    {
        return fileObjects?.find { it.type == type }
    }
}
