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
                    result.pqeValid    = false
                    result.pqeMessage  = "No Surface Information available"
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
                    result.projType=projType

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
    def imagePointsToGround(IptsToGrdCommand cmd)//def filename, def pointList, def entryId)
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
        Double angInc = cmd.pqeEllipseAngularIncrement?:10
        int numPnts = 360/angInc + 1

        double [] ellSamp = new double[numPnts]
        double [] ellLine = new double[numPnts]
        try
        {
            if ( imageSpaceModel.setModelFromFile(cmd.filename, cmd.entryId) )
            {
                cmd.pointList.each{pt->
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

                    if(cmd.pqeIncludePositionError)
                    {
                        HashMap pqe = getPositionError(imageSpaceModel,
                                accuracyInfo,
                                gpt,
                                cmd.pqeProbabilityLevel?:0.9,
                                angInc,
                                pqeArray,
                                ellSamp,
                                ellLine)
                        if(pqe)
                        {

                            if(numPnts&&cmd.pqeEllipsePointType)
                            {
                                switch(cmd.pqeEllipsePointType?.toLowerCase())
                                {
                                    case "array":
                                        pqe.ellPts = []
                                        for(int i = 0; i < numPnts; i++)
                                        {
                                            pqe.ellPts << [x: ellSamp[i], y: ellLine[i]]
                                        }
                                        break

                                    case "polygon":
                                        pqe.ellPts = "POLYGON(("
                                        Integer nPointMinus1 = numPnts-1
                                        for(int i = 0; i < nPointMinus1; i++)
                                        {
                                            pqe.ellPts+="${ellSamp[i]} ${ellLine[i]},".toString()
                                        }
                                        pqe.ellPts+="${ellSamp[nPointMinus1]} ${ellLine[nPointMinus1]}))".toString()
                                        break
                                    case "linestring":
                                        pqe.ellPts = "LINESTRING("
                                        Integer nPointMinus1 = numPnts-1
                                        for(int i = 0; i < nPointMinus1; i++)
                                        {
                                            pqe.ellPts+="${ellSamp[i]} ${ellLine[i]},".toString()
                                        }
                                        pqe.ellPts +="${ellSamp[nPointMinus1]} ${ellLine[nPointMinus1]})".toString()
                                        break
                                    default:
                                        break
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

    /**
     * @param filename
     * @param pointList List of points of
     * @param entryId
     * @return
     */
    def groundToImagePoints(GrdToIptsCommand cmd)
    {
        def result = [status:HttpStatus.OK,
                      statusMessage:"",
                      data:[]];
        def imageSpaceModel = new ImageModel()
        def imagePoint = new ossimDpt(0.0,0.0);
        def groundPoint = new ossimGpt()
        def geodeticEvaluator = new GeodeticEvaluator()
        try{
            if ( imageSpaceModel.setModelFromFile(cmd.filename, cmd.entryId) )
            {
                cmd.pointList.each{pt->
                    groundPoint.makeNan();
                    groundPoint.latd = pt.lat as double;
                    groundPoint.lond = pt.lon as double;
                    if(!pt.hgt)
                    {
                        pt.hgt = geodeticEvaluator.getHeightEllipsoid(groundPoint)
                        if(pt.hgt?.naN)
                        {
                            pt.hgt=0.0
                        }
                    }
                    if(pt.hgt)
                    {
                        groundPoint.height =  pt.hgt

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
                    result.data<<([x:imagePoint.x,
                                   y:imagePoint.y,
                                   lat:groundPoint.latd(),
                                   lon:groundPoint.lond(),
                                   hgt:groundPoint.height()]);
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
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.statusMessage = e.toString()
        }
        finally{
            imagePoint?.delete()
            groundPoint?.delete()
            imageSpaceModel?.delete()
            geodeticEvaluator?.delete()
        }
        result;
    }
}

