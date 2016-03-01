package ossimtools.app

import geoscript.geom.Bounds
import geoscript.layer.GeoTIFF
import geoscript.render.Map as GeoScriptMap

import joms.oms.OssimTools
import geoscript.geom.Point
import geoscript.proj.Projection
import omar.oms.ChipperUtil

import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.Raster

import javax.imageio.ImageIO

class OssimToolsService
{
	static transactional = false

	def grailsApplication

	def calculateExtent( def lat, def lon, def radius )
	{
		def point1 = new geoscript.geom.Point( lon, lat )
		def point2 = Projection.transform( point1, 'epsg:4326', 'epsg:3857' )
		def bounds = /*Projection.transform(*/ point2.buffer( radius )/*, 'epsg:3857', 'epsg:4326' )*/.bounds


		[ [ bounds.minX, bounds.minY ], [ bounds.maxX, bounds.maxY ] ]
	}

	synchronized  def execTool( def params )
	{
	  println "\n ";println "OssimToolsService.execTool() -- params:"
	  println params; println "\n "
	
		def ossimTool = new OssimTools( params.name );
	  println "AAAAAAAAAAAAAAA"; println "\n "
		if ( !ossimTool )
		{
			return
		}
	  println "BBBBBBBBBBBBBB"; println "\n "
    def ossimMap = [
       aoi_geo_center:  [ params.lat, params.lon ].join(' '),
       aoi_map_rect: params.BBOX,
       observer:  [ params.lat, params.lon ].join(' '),
     visibility_radius: params.radiusROI,
       lut_file:  "${grailsApplication.config.ossimtools.supportData}/vs.lut" as String,
       height_of_eye: params.heightOfEye,
       //lz_min_radius:  params.radiusLZ,
       //roughness_threshold: params.roughness,
       //slope_threshold: params.slope,
       srs: params.SRS.split(':').last()
    ]
	  println "\n" ;println "OssimToolsService.execTool() -- ossimMap:"
	  println ossimMap;println "\n"

		def hints = [ transparent: false,
		              width: params.WIDTH.toInteger(),
		              height: params.HEIGHT.toInteger() ]
    int numBands = 3
    def raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE,
				hints.width, hints.height,
				hints.width * numBands, numBands, ( 0..<numBands ) as int[],
				new Point( 0, 0 ) )

		// Eventually need to let the user select colors. For now use hardcoded LUT on server:
		params.lut = grailsApplication?.config?.ossimtools?.supportData?.toString() + "hlz.lut"
	  println "CCCCCCCCCCCCCC"; println "\n "
		if ( !ossimTool.initialize( ossimMap ) )
		{
			return
		};
	  println "DDDDDDDDDDDDDDD"; println "\n "
		// Since a new tool is created each time, pass in a bogus map to indicate full AOI should
		// be computed:
		def bbox = params.BBOX.split(',')
		def hintsMap = [
				min_x : bbox[0],
				min_y : bbox[1],
				max_x : bbox[2],
				max_y : bbox[3],
				width: params.WIDTH,
				height: params.HEIGHT,
		]
		if ( !ossimTool.getChip( raster.dataBuffer.data, hintsMap ) )
		{
			return
		}
	  println "EEEEEEEEEEEEEEEE"; println "\n "
		ossimTool?.delete()

		def colorModel = ChipperUtil.createColorModel(numBands, hints?.transparent)
		def image = new BufferedImage(colorModel, raster, false, null)
		def ostream = new ByteArrayOutputStream()
	  println "FFFFFFFFFFFFFFFF"; println "\n "
		ImageIO.write(image, 'png', ostream)
	  println "GGGGGGGGGGGGGGG"; println "\n "
    [contentType: 'image/png', buffer: ostream.toByteArray() ]
  }

	def renderHillShade(def params)
	{
		def file = grailsApplication?.config?.ossimtools?.hillShade?.toString() as File
		def geotiff = new GeoTIFF( file )
		def raster = geotiff.read()
		def ostream = new ByteArrayOutputStream()
		def bounds = new Bounds( *( params['BBOX'].split( ',' )*.toDouble() ), params['SRS'] )

		def map = new GeoScriptMap(
				layers: [raster],
				width: params['WIDTH'].toInteger(),
				height: params['HEIGHT'].toInteger(),
				bounds: bounds,
				proj: bounds.proj,
				type: 'png'
		)

		map.render( ostream )
		map.close()
		raster?.dispose()

		[contentType: 'image/png', buffer: ostream.toByteArray()]

	}


}
