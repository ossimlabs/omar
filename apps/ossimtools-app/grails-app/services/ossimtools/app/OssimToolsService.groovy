package ossimtools.app

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
	  println params  
	
		def ossimTool = new OssimTools( params.name );
		if ( !ossimTool )
		{
			return
		}

    def ossimMap = [
       aoi_geo_center:  [ params.lat, params.lon ].join(' '),
       aoi_size_meters: [ params.radiusROI, params.radiusROI].join(' '),
       lut_file:  "${grailsApplication.config.ossimtools.supportData}/vs.lut" as String,
       //lz_min_radius:  params.radiusLZ,
       //roughness_threshold: params.roughness,
       //slope_threshold: params.slope,
       srs: params.SRS.split(':').last()
    ]
 	  println ossimMap  

		def hints = [ transparent: true,
		              width: params.WIDTH.toInteger(),
		              height: params.HEIGHT.toInteger() ]
    int numBands = 4
    def raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE,
				hints.width, hints.height,
				hints.width * numBands, numBands, ( 0..<numBands ) as int[],
				new Point( 0, 0 ) )

		// Eventually need to let the user select colors. For now use hardcoded LUT on server:
		params.lut = grailsApplication?.config?.ossimtools?.supportData?.toString() + "hlz.lut"

		if ( !ossimTool.initialize( ossimMap ) )
		{
			return
		};

		// Since a new tool is created each time, pass in a bogus map to indicate full AOI should
		// be computed:
		def bogusMap = [ 'foo' : 'bar']
		if ( !ossimTool.getChip( raster.dataBuffer.data, bogusMap ) )
		{
			return
		}

		def colorModel = ChipperUtil.createColorModel(numBands, hints?.transparent)
		def image = new BufferedImage(colorModel, raster, false, null)
		def ostream = new ByteArrayOutputStream()

		ImageIO.write(image, 'png', ostream)

    [contentType: 'image/png', buffer: ostream.toByteArray() ]
  }
}
