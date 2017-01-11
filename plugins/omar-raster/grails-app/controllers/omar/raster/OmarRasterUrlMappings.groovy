package omar.raster

class OmarRasterUrlMappings {

    static mappings = {
        "/dataManager/addRaster"(controller: 'rasterDataSet', action: 'addRaster')
        "/dataManager/removeRaster"(controller: 'rasterDataSet', action: 'removeRaster')
        "/dataManager/getDistinctValues"(controller: 'rasterDataSet', action: 'getDistinctValues')
        "/dataManager/getRasterFilesProcessing"(controller: 'rasterDataSet', action: 'getRasterFilesProcessing')
        "/dataManager/getRasterFiles"(controller: 'rasterDataSet', action: 'getRasterFiles')    }
}
