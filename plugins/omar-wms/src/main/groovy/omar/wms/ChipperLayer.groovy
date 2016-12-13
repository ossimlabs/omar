package omar.wms

import geoscript.geom.Bounds
import omar.core.HttpStatus
import omar.oms.JaiImage
import org.geotools.geometry.jts.ReferencedEnvelope
import org.geotools.map.DirectLayer
import org.geotools.map.MapContent
import org.geotools.map.MapViewport

import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.geom.AffineTransform

import omar.oms.ImageInfoUtil
import omar.oms.ChipperUtil

import java.awt.image.BufferedImage

/**
 * Created by sbortman on 1/19/16.
 */
class ChipperLayer extends DirectLayer
{
  private static final identity = new AffineTransform()

  Map<String, String> initOpts = [:]
  Map<String, Object> initHints = [:]
  Bounds bbox

  public ChipperLayer(String imageFile, int entry = 0)
  {
    def info = ImageInfoUtil.getImageInfoAsMap( imageFile )

    bbox = new Bounds(
            info."image${entry}".geometry.ll_lon.toDouble(),
            info."image${entry}".geometry.ll_lat.toDouble(),
            info."image${entry}".geometry.ur_lon.toDouble(),
            info."image${entry}".geometry.ur_lat.toDouble(),
            'epsg:4326' )

    initOpts = [
            operation: 'ortho',
            hist_op: 'auto-minmax',
            srs: 'epsg:4326',
            output_radiometry: 'U8',
            three_band_out: 'true',
            'image0.file': imageFile,
            'image0.entry': String.valueOf( entry )
    ]

    initHints = [transparent: true]
  }

  public ChipperLayer(List images)
  {
    initOpts = [
            operation: 'ortho',
            hist_op: 'auto-minmax',
            srs: 'epsg:4326',
            output_radiometry: 'U8',
            three_band_out: 'true',
    ]

    images?.eachWithIndex { image, i ->

      if(image.imageFile)
      {
        def imageFile = image.imageFile
        def entry = ( image.entry ) ? String.valueOf( image.entry ) : '0'

        if(image.groundGeom)
        {
          Bounds imageBounds = image.groundGeom.bounds
          if(bbox)
          {
            bbox.expand(imageBounds)
          }
          else
          {
            bbox =  imageBounds
          }
        }
        else
        {
          def info = ImageInfoUtil.getImageInfoAsMap( imageFile )
          def imageKey = "image${entry}" as String

          if ( bbox )
          {
            bbox.expand( new Bounds(
                    info[imageKey].geometry.ll_lon.toDouble(),
                    info[imageKey].geometry.ll_lat.toDouble(),
                    info[imageKey].geometry.ur_lon.toDouble(),
                    info[imageKey].geometry.ur_lat.toDouble(),
                    'epsg:4326' ) )
          }
          else
          {
            bbox = new Bounds(
                    info[imageKey].geometry.ll_lon.toDouble(),
                    info[imageKey].geometry.ll_lat.toDouble(),
                    info[imageKey].geometry.ur_lon.toDouble(),
                    info[imageKey].geometry.ur_lat.toDouble(),
                    'epsg:4326' )
          }
        }
        initOpts["image${i}.file"] = imageFile
        initOpts["image${i}.entry"] = entry
      }

    }

    initHints = [transparent: true]
  }

  @Override
  public ReferencedEnvelope getBounds()
  {
    println "bbox?.env ==== ${bbox?.env}"
    return bbox?.env
  }

  @Override
  public void draw(Graphics2D graphics, MapContent mapContent, MapViewport viewport)
  {
    def screenArea = viewport.screenArea
    def bbox = viewport.bounds

    def renderOpts = initOpts + [
            cut_width: screenArea.@width as String,
            cut_height: screenArea.@height as String,
            cut_wms_bbox: [bbox.minX, bbox.minY, bbox.maxX, bbox.maxY].join( ',' ),
            srs: viewport.coordinateReferenceSystem.identifiers.first() as String
    ]

    def renderHints = initHints + [
            width: screenArea.@width,
            height: screenArea.@height
    ]

    HashMap chipperResult = ChipperUtil.runChipper(renderOpts)
    def image

    if(chipperResult.raster)
    {
      if (chipperResult.raster.numBands > 3)
      {
        def planarImage = JaiImage.bufferedToPlanar(new BufferedImage(chipperResult.colorModel, chipperResult.raster, true, null))
        planarImage.data
        def modifiedImage = JaiImage.selectBandsForRendering(planarImage)

        if (modifiedImage)
        {
          chipperResult.raster = modifiedImage.data
          chipperResult.colorModel = modifiedImage.colorModel
        }

      }


      try
      {
        image = ChipperUtil.optimizeRaster(chipperResult.raster, chipperResult.colorModel, renderHints)
      }
      catch (e)
      {
      }
    }
//    def image = ChipperUtil.createImage( renderOpts, renderHints )

      if(image) graphics.drawRenderedImage( image, identity )
    }

    @Override
    public void dispose()
    {
      super.preDispose()
      super.dispose()
    }
  }