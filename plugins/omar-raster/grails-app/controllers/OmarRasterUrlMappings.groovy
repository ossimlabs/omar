class OmarRasterUrlMappings {

    static mappings = {
        "/dataManager/addRaster"(controller: 'rasterDataSet', action: 'addRaster')
        "/dataManager/removeRaster"(controller: 'rasterDataSet', action: 'removeRaster')
    }
}
