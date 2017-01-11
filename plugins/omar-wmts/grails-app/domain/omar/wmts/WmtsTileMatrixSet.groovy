package omar.wmts

import geoscript.geom.Bounds
import geoscript.proj.Projection

class WmtsTileMatrixSet {
    String name
    Double minX
    Double minY
    Double maxX
    Double maxY

    Integer minLevel
    Integer maxLevel

    Integer tileWidth
    Integer tileHeight

    String epsgCode

    // not sure if I want this relation or not
    static hasMany = [wmtsLayer:WmtsLayer]

    static constraints = {
        name unique:true
        minX nullable:false, blank:false
        minY nullable:false, blank:false
        maxX nullable: false, blank:false
        maxY nullable:false, blank:false

        minLevel nullable: false, blank: false
        maxLevel nullable: false, blank: false

        tileWidth nullable: false, blank: false
        tileHeight nullable: false, blank: false

        epsgCode nullable: false, blank: false
    }
    static mapping = {
        cache true
        id generator: 'identity'
        name index: 'omar_wmts_tile_matrix_set_name_idx'
    }

    Bounds getBounds()
    {
        Bounds result

        if(epsgCode) result = new Bounds ( minX, minY, maxX, maxY, new Projection(epsgCode))

        result
    }

    TileMatrixSet toTileMatrixSet()
    {
        TileMatrixSet result = new TileMatrixSet(identifier: this.name,
                                                bounds: bounds,
                                                minLevel: minLevel,
                                                maxLevel:maxLevel,
                                                tileWidth:tileWidth,
                                                tileHeight:tileHeight)

        result
    }
}
