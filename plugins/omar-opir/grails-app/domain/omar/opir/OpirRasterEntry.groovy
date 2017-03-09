package omar.opir

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
//import org.hibernate.spatial.GeometryType

class OpirRasterEntry {
    String filename
    String entryId
    Integer numberOfBands

    Integer numberOfResLevels
    String gsdUnit
    Double gsdX
    Double gsdY

    Integer bitDepth
    String dataType
    String tiePointSet
    String indexId
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

    static belongsTo = [rasterDataSet: OpirRasterDataSet]

    static hasMany = [fileObjects: OpirRasterEntryFile]

    static mapping = {
        cache true
        id generator: 'identity'
        filename index: 'opir_raster_entry_filename_idx'
        width index: 'opir_raster_entry_width_idx'
        height index: 'opir_raster_entry_height_idx'
        numberOfBands index:'opir_raster_entry_number_of_bands_idx'
        bitDepth index: 'opir_raster_entry_bit_depth_idx'
        dataType index: 'opir_raster_entry_data_type_idx'
        indexId index: 'opir_raster_entry_index_id_idx', unique:true
        entryId index: 'opir_raster_entry_entry_id_idx'
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
        groundGeom /*type: GeometryType, */ sqlType: 'geometry(MultiPolygon, 4326)'
        accessDate index: 'opir_raster_entry_access_date_idx'
        ingestDate index: 'opir_raster_entry_ingest_date_idx'
        receiveDate index: 'opir_raster_entry_receive_date_idx'

    }
    static constraints = {
        entryId(unique:false)
        filename(nullable: false)
        width( min: 0 )
        height( min: 0 )
        numberOfBands( min: 0 )
        bitDepth( min: 0 )
        dataType()

        numberOfResLevels( nullable: true )
        gsdUnit( nullable: true )
        gsdX( nullable: true )
        gsdY( nullable: true )
        indexId( nullable: false, unique: false, blank: false )
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

    def beforeInsert = {
        if ( !ingestDate )
        {
            // ingestDate = new DateTime(DateTimeZone.UTC);
            ingestDate = Calendar.getInstance(TimeZone.getTimeZone('GMT')).time.toTimestamp()

            if ( !indexId )
            {
                def mainFile = rasterEntry.rasterDataSet.getFileFromObjects( "main" )
                if ( mainFile )
                {
                    def value = "${entryId}-${mainFile}"
                    indexId = mainFile.omarIndexId;
                }
            }
        }
    }
    def adjustAccessTimeIfNeeded(def everyNHours = 24)
    {
        if ( !accessDate )
        {
            // accessDate = new DateTime(DateTimeZone.UTC);
            accessDate = Calendar.getInstance(TimeZone.getTimeZone('GMT')).time.toTimestamp()
        }
        else
        {
            // DateTime current = new DateTime(DateTimeZone.UTC);
            // long currentAccessMil = accessDate.getMillis()
            // long currentMil = current.getMillis()

            def current = Calendar.getInstance(TimeZone.getTimeZone('GMT')).time.toTimestamp()
            long currentAccessMil = accessDate.time()
            long currentMil = current.time()

            double millisPerHour = 3600000 // 60*60*1000  <seconds>*<minutes in an hour>*<milliseconds>
            double hours = ( currentMil - currentAccessMil ) / millisPerHour
            if ( hours > everyNHours )
            {
                accessDate = current
            }
        }
    }
    def getFileFromObjects(def type)
    {
        return fileObjects?.find { it.type == type }
    }

    def getMetersPerPixel()
    {
        // need to check unit type but for mow assume meters
        return gsdY; // use Y since X may decrease along lat.
    }

    def getMainFile()
    {
        def mainFile = null//rasterDataSet?.fileObjects?.find { it.type == 'main' }

        if ( !mainFile )
        {
            //mainFile = org.ossim.omar.raster.RasterFile.findByRasterDataSetAndType(rasterDataSet, "main")

            mainFile = OpirRasterFile.createCriteria().get {
                eq( "type", "main" )
                createAlias( "rasterDataSet", "d" )
                eq( "rasterDataSet", this.rasterDataSet )
            }

        }

        return mainFile
    }
    def getAssociationType(def type)
    {
        def tempFile = RasterEntryFile.createCriteria().get {
            eq( "type", "${type}" )
            createAlias( "rasterEntry", "r" )
            eq( "rasterEntry", this )
        }

        tempFile;
    }

    def getHistogramFile()
    {
        def result = getFileFromObjects( "histogram" )?.name
        if ( !result )
        {
            result = mainFile?.name
            if ( result )
            {
                def nEntries = rasterDataSet?.rasterEntries?.size() ?: 1
                def ext = result.substring( result.lastIndexOf( "." ) )
                if ( ext )
                {
                    if ( nEntries > 1 )
                    {
                        result = result.replace( ext, "_e${entryId}.his" )
                    }
                    else
                    {
                        result = result.replace( ext, ".his" )
                    }
                }
                else
                {
                    if ( nEntries > 1 )
                    {
                        result = result + "_e${entryId}.his"
                    }
                    else
                    {
                        result = result + ".his"
                    }
                }
            }
        }

        result
    }

    static OpirRasterEntry initRasterEntry(def rasterEntryNode, OpirRasterEntry rasterEntry = null)
    {
        rasterEntry = rasterEntry ?: new OpirRasterEntry()

        rasterEntry.entryId = rasterEntryNode.entryId
        rasterEntry.width = rasterEntryNode?.width?.toLong()
        rasterEntry.height = rasterEntryNode?.height?.toLong()
        rasterEntry.numberOfBands = rasterEntryNode?.numberOfBands?.toInteger()
        rasterEntry.numberOfResLevels = rasterEntryNode?.numberOfResLevels?.toInteger()
        rasterEntry.bitDepth = rasterEntryNode?.bitDepth?.toInteger()
        rasterEntry.dataType = rasterEntryNode?.dataType
        
        if ( rasterEntryNode?.TiePointSet )
        {
            rasterEntry.tiePointSet = "<TiePointSet><Image><coordinates>${rasterEntryNode?.TiePointSet.Image.coordinates.text().replaceAll( "\n", "" )}</coordinates></Image>"
            rasterEntry.tiePointSet += "<Ground><coordinates>${rasterEntryNode?.TiePointSet.Ground.coordinates.text().replaceAll( "\n", "" )}</coordinates></Ground></TiePointSet>"
        }
    }
}
