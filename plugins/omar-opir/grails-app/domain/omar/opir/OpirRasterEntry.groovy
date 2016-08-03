package omar.opir

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
import org.hibernate.spatial.GeometryType

class OpirRasterEntry {
    String filename
    String mission
    Date acquisitionStart
    Date acquisitionEnd
    String taskName
    Integer focalPlaneId
    Double frameRate
    Double scanRate
    String hdfLocation
    Integer filter1
    Integer filter2
    Integer spatialSummation
    Integer snapshotSize
    Integer summedScans
    Integer gainSetting
    Integer integrationIndex
    Integer width
    Integer height
    Double metersPerPixel
    MultiPolygon groundGeom
    Date accessDate
    Date ingestDate
    Date receiveDate

    static belongsTo = [opirRasterDataSet: OpirRasterDataSet]

    static hasMany = [fileObjects: OpirRasterEntryFile]

    static mapping = {
        filename index: 'opir_raster_entry_filename_idx'
        mission index: 'opir_raster_entry_mission_idx'
        acquisitionStart index: 'opir_raster_entry_acquisition_start_idx'
        acquisitionEnd index: 'opir_raster_entry_acquistion_end_idx'
        taskName index: 'opir_raster_entry_task_name_idx'
        focalPlaneId index: 'opir_raster_entry_focal_plane_idx'
        frameRate index: 'opir_raster_entry_frame_rate_idx'
        scanRate index: 'opir_raster_entry_scan_rate_idx'
        hdfLocation index: 'opir_raster_entry_hdf_location_idx'
        filter1 index: 'opir_raster_entry_filter1_idx'
        filter2 index: 'opir_raster_entry_filter2_idx'
        spatialSummation index: 'opir_raster_entry_spatial_summation_idx'
        snapshotSize index: 'opir_raster_entry_hdf_snapshot_size_idx'
        summedScans index: 'opir_raster_entry_summed_scans_idx'
        gainSetting index: 'opir_raster_entry_gain_setting_idx'
        integrationIndex index: 'opir_raster_entry_integration_idx'
        width index: 'opir_raster_entry_width_idx'
        height index: 'opir_raster_entry_height_idx'
        metersPerPixel index: 'opir_raster_entry_meters_per_pixel_idx'
        groundGeom type: GeometryType, sqlType: 'geometry(MultiPolygon, 4326)'
        accessDate index: 'opir_raster_entry_access_date_idx'
        ingestDate index: 'opir_raster_entry_ingest_date_idx'
        receiveDate index: 'opir_raster_entry_receive_date_idx'

    }
    static constraints = {
        filename(nullable: false)
        mission(nullable:true)
        acquisitionStart(nullable:true)
        acquisitionEnd(nullable:true)
        taskName(nullable:true)
        focalPlaneId(nullable:true)
        frameRate(nullable:true)
        scanRate(nullable:true)
        hdfLocation(nullable:true)
        filter1(nullable:true)
        filter2(nullable:true)
        spatialSummation(nullable:true)
        snapshotSize(nullable:true)
        summedScans(nullable:true)
        gainSetting(nullable:true)
        integrationIndex(nullable:true)
        width(nullable:true)
        height(nullable:true)
        metersPerPixel(nullable:true)
        groundGeom( nullable: false )
        accessDate( nullable: false )
        ingestDate( nullable: false )
        receiveDate( nullable: false )
    }
}
