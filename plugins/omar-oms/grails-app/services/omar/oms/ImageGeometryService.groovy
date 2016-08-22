package omar.oms

import joms.oms.ImageModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.GeodeticEvaluator
import joms.oms.ossimString
import joms.oms.ossimElevationAccuracyInfo
import joms.oms.MapProjection
import joms.oms.ossimEcefPoint
import omar.core.HttpStatus

import grails.transaction.Transactional

@Transactional
class ImageGeometryService {

    /**
     * @param filename Filename to use as input
     * @param pointList List of points in the form of [ {x:,y:}, ..... {}]
     * @param entryId The entry id of the file to use as input
     * @return
     */
    def iptsToGrd(IptsToGrdCommand cmd)//def filename, def pointList, def entryId)
    {
        def result = [status:HttpStatus.OK,
                      statusMessage:"",
                      data:[]];
        def imageSpaceModel = new ImageModel()
        def geodeticEvaluator = new GeodeticEvaluator()
        def dpt = new ossimDpt(0.0,0.0);
        def gpt = new ossimGpt()

        try
        {
            if ( imageSpaceModel.setModelFromFile(cmd.filename, cmd.entryId) )
            {
                cmd.ipts.each{pt->
                    dpt.x = pt.x as double;
                    dpt.y = pt.y as double;
                    imageSpaceModel.imageToGround(dpt,
                            gpt) ;
                    if(gpt.isHgtNan())
                    {
                        gpt.height = 0.0;
                    }

                    Double hgtMsl = geodeticEvaluator.getHeightMSL(groundPoint);
                    if(hgtMsl.naN)
                    {
                        hgtMsl = 0.0;
                    }
                    result.data<<([x:dpt.x,
                                   y:dpt.y,
                                   lat:gpt.latd(),
                                   lon:gpt.lond(),
                                   hgt:gpt.height(),
                                   hgtMsl:hgtMsl]);
                }
            }
            else
            {
                result.status = HttpStatus.NOT_FOUND
                result.statusMessage = "Unable to set Model from file ${cmd.filename}"
            }
        }
        catch(e)
        {
            result.statusMessage = "${e.toString()}".toString()
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.data = []
        }
        imageSpaceModel?.delete()
        geodeticEvaluator?.delete()
        imagePoint?.delete()
        groundPoint?.delete()
        result;
    }


    /**
     * @param filename
     * @param pointList List of points of
     * @param entryId
     * @return
     */

    /*
    def groundSpaceListToImageSpace(def filename, def pointList, def entryId)
    {
       def result = [];
       def imageSpaceModel = new ImageModel()
       def imagePoint = new ossimDpt(0.0,0.0);
       def groundPoint = new ossimGpt()
       if ( imageSpaceModel.setModelFromFile(filename, entryId) )
       {
          pointList.each{pt->
             groundPoint.makeNan();
             groundPoint.latd = pt.lat as double;
             groundPoint.lond = pt.lon as double;
             if(pt.hgt)
             {
                groundPoint.height = pt.hgt;
             }
             imageSpaceModel.groundToImage(groundPoint,
                     imagePoint) ;
             if(imagePoint.hasNans())
             {
                imagePoint.x = 0;
                imagePoint.y = 0;
             }
             if(groundPoint.isHgtNan())
             {
                groundPoint.height = 0.0;
             }
             result.add([x:imagePoint.x,
                         y:imagePoint.y,
                         lat:groundPoint.latd(),
                         lon:groundPoint.lond(),
                         hgt:groundPoint.height()]);
          }
       }

       result;
    }
    */
}

