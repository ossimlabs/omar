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
    HashMap getPositionError(ImageModel imageSpaceModel,
                             ossimElevationAccuracyInfo accuracyInfo,
                             ossimGpt gpt,
                             Double probabilityLevel,
                             Double angInc,
                             def pqeArray,
                             def ellSamp,
                             def ellLine)
    {
        HashMap result = [:]
        Boolean errorPropAvailable = imageSpaceModel.imageToGroundErrorPropagation(gpt,
                                probabilityLevel,
                                angInc,
                                pqeArray,
                                ellSamp,
                                ellLine)
        def typeString = new ossimString()
        String projType
        Double nPoints = ellSamp.size()
        try{
            if (errorPropAvailable)
            {
                // Get surface info
                Boolean surfaceInfoAvailable = imageSpaceModel.getProjSurfaceInfo(gpt, accuracyInfo)
                String surfaceName = accuracyInfo.surfaceName
                // Get projection info
                typeString = imageSpaceModel.getType()
                projType = typeString
                projType = projType.minus("ossim")
                if (!surfaceInfoAvailable)
                {
                    result.surfaceName = "NO SURFACE INFO"
                }
                else
                {
                    result.surfaceName = surfaceName
                    result.CE=pqeArray[0]
                    result.LE=pqeArray[1]
                    result.SMA=pqeArray[2]
                    result.SMI=pqeArray[3]
                    result.AZ=Math.toDegrees(pqeArray[4])
                    result.probabilityLevel=probabilityLevel

                    //dataRecord.nELL=pqeArray[5]
                }
            }
        }
        catch(e)
        {
            result = [:]
        }
        finally
        {
            typeString?.delete()
        }
        result
    }
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
        def accuracyInfo = new ossimElevationAccuracyInfo()
        def imageSpaceModel = new ImageModel()
        def geodeticEvaluator = new GeodeticEvaluator()
        def dpt = new ossimDpt(0.0,0.0);
        def gpt = new ossimGpt()
        double [] pqeArray = new double[6]
        Double angInc = 10
        int numPnts = 360/angInc + 1
        double [] ellSamp = new double[numPnts]
        double [] ellLine = new double[numPnts]
        try
        {
            if ( imageSpaceModel.setModelFromFile(cmd.filename, cmd.entryId) )
            {
                cmd.ipts.each{pt->
                    dpt.x = pt.x as double
                    dpt.y = pt.y as double
                    imageSpaceModel.imageToGround(dpt,
                            gpt) ;
                    Double ellipsHeight
                    Double hgtMsl = geodeticEvaluator.getHeightMSL(gpt);

                    if(gpt.isHgtNan())
                    {
                        ellipsHeight = geodeticEvaluator.getHeightEllipsoid(gpt)
                        if(!ellipsHeight.naN)
                        {
                            gpt.height = ellipsHeight
                            if(hgtMsl.naN)
                            {
                                hgtMsl = 0.0;
                            }
                        }
                        else
                        {
                            gpt.height = 0.0
                        }
                    }
                    else if(hgtMsl.naN)
                    {
                        hgtMsl = 0.0;
                    }
                    def dataRecord = [x:dpt.x,
                                      y:dpt.y,
                                      lat:gpt.latd(),
                                      lon:gpt.lond(),
                                      hgt:gpt.height,
                                      hgtMsl:hgtMsl]

                    if(cmd.pqe?.includePositionError)
                    {
                        HashMap pqe = getPositionError(imageSpaceModel,
                                                        accuracyInfo,
                                                        gpt,
                                                        cmd.pqe?.probabilityLevel?:0.9,
                                                        angInc,
                                                        pqeArray,
                                                        ellSamp,
                                                        ellLine)
                        if(pqe)
                        {

                            if(numPnts)
                            {
                                if(cmd.pqe?.ellipsePointType == "array")
                                {
                                    pqe.ellPts = []
                                    for(int i = 0; i < numPnts; i++)
                                    {
                                        pqe.ellPts << [x: ellSamp[i], y: ellLine[i]]
                                    }
                                }
                            }

                            dataRecord.pqe = pqe
                        }
                    }
                    result.data<<dataRecord;
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
        finally
        {
            imageSpaceModel?.delete()
            geodeticEvaluator?.delete()
            dpt?.delete()
            gpt?.delete()
            accuracyInfo?.delete()

            imageSpaceModel = null
            geodeticEvaluator = null
            dpt = null
            gpt = null
            accuracyInfo = null
        }
        result
    }


    // The following code is cut from OLDMAR distribution and will be refactored once we are ready to add error
    // calculation in O2
    //
//    /**
//     * @brief Single-ray projection with RPC error propagation
//     * @param filename
//     * @param imgPt
//     * @param probLev probability level (.5,.9,.95)
//     * @param angInc angular increment (deg) for image space ellipse points
//     * @param entryId
//     * @return
//     */
//    def imageSpaceToGroundSpace(def filename, def imgPt, def probLev, def angInc, def entryId)
//    {
//        def result = [];
//        def ellPts = [];
//
//        def imageSpaceModel = new ImageModel()
//        def geodeticEvaluator = new GeodeticEvaluator()
//        def imagePoint = new ossimDpt(imgPt.x as double, imgPt.y as double)
//        def groundPoint = new ossimGpt()
//        boolean errorPropAvailable = false
//
//        boolean surfaceInfoAvailable = false
//        def accuracyInfo = new ossimElevationAccuracyInfo()
//        def typeString   = new ossimString()
//        String projType
//
//        int numPnts = 360/angInc + 1
//        double [] ellSamp = new double[numPnts]
//        double [] ellLine = new double[numPnts]
//        double [] pqeArray = new double[6]
//        String hgtMSL
//        String hgtHAE
//
//        if ( imageSpaceModel.setModelFromFile(filename, entryId) )
//        {
//            // Perform projection
//            imageSpaceModel.imageToGround(imagePoint, groundPoint)
//            if(groundPoint.isHgtNan())
//            {
//
//                Double ellipsHeight =  geodeticEvaluator.getHeightEllipsoid(groundPoint)
//                if(!ellipsHeight.naN)
//                {
//                    def hgtM = geodeticEvaluator.getHeightMSL(groundPoint)
//                    def hgtE   = ellipsHeight
//                    groundPoint.height = ellipsHeight
//                    hgtHAE = Double.toString(hgtE)
//                    hgtMSL = Double.toString(hgtM)
//                }
//                else
//                {
//                    hgtHAE = "---";
//                    hgtMSL = "---";
//                    groundPoint.height = 0.0
//                }
//            }
//            else
//            {
//                def hgtE = groundPoint.height()
//                def hgtM = geodeticEvaluator.getHeightMSL(groundPoint)
//                hgtHAE = Double.toString(hgtE)
//                hgtMSL = Double.toString(hgtM)
//            }
//
//
//            // Get projection info
//            typeString = imageSpaceModel.getType()
//            projType = typeString
//            projType = projType.minus("ossim")
//
//            // Perform error propagation
//            errorPropAvailable =
//                    imageSpaceModel.imageToGroundErrorPropagation(groundPoint,
//                            probLev as double,
//                            angInc as double,
//                            pqeArray,
//                            ellSamp,
//                            ellLine)
//        }
//
//        // Get surface info
//        surfaceInfoAvailable = imageSpaceModel.getProjSurfaceInfo(groundPoint, accuracyInfo)
//        String info
//        info = accuracyInfo.surfaceName
//        accuracyInfo.delete()
//        accuracyInfo = null
//        if (!surfaceInfoAvailable)
//        {
//            info = "NO SURFACE INFO"
//        }
//
//        if (errorPropAvailable)
//        {
//            for(int i = 0; i < numPnts; i++){
//                ellPts << [xe: ellSamp[i], ye: ellLine[i]]
//            }
//
//            result = [x:      imgPt.x,
//                      y:      imgPt.y,
//                      lat:    groundPoint.latd(),
//                      lon:    groundPoint.lond(),
//                      hgt:    hgtHAE,
//                      type:   projType,
//                      hgtMsl: hgtMSL,
//                      sInfo:  info,
//                      CE:     pqeArray[0],
//                      LE:     pqeArray[1],
//                      SMA:    pqeArray[2],
//                      SMI:    pqeArray[3],
//                      AZ:     Math.toDegrees(pqeArray[4]),
//                      lvl:    probLev,
//                      nELL:   pqeArray[5]]
//        }
//        else
//        {
//            result = [x:      imgPt.x,
//                      y:      imgPt.y,
//                      lat:    groundPoint.latd(),
//                      lon:    groundPoint.lond(),
//                      hgt:    hgtHAE,
//                      type:   projType,
//                      hgtMsl: hgtMSL,
//                      sInfo:  info];
//        }
//
//        imageSpaceModel.delete()
//        geodeticEvaluator.delete()
//        groundPoint.delete();
//        groundPoint = null;
//
//        [ellpar: result, ellpts: ellPts]
//    }


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

