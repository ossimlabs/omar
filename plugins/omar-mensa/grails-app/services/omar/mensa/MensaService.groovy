package omar.mensa

import grails.transaction.Transactional
import joms.oms.ImageModel
import geoscript.geom.Geometry
import omar.core.HttpStatus
import joms.oms.GeodeticEvaluator
import joms.oms.ossimString
import joms.oms.ossimElevationAccuracyInfo
import joms.oms.MapProjection
import joms.oms.ossimEcefPoint
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import omar.oms.IptsToGrdCommand

@Transactional
class MensaService {

    def imageGeometryService

    def calculateImageDistance(DistanceCommand cmd)
    {
        HashMap result = [status:HttpStatus.OK,
                          statusMessage:"",
                          data:[gdist:0.0, distance:0.0, unit: "m"]
        ]
        def geom = null
        def imagePoint = new ossimDpt(0.0, 0.0)
        def groundPoint = new ossimGpt()
        def ecefPoint = new ossimEcefPoint()
        def lastGroundPoint;
        def distance = 0.0;
        def gdist = 0.0;
        def area = 0.0;
        def coordinateList = []
        def utmCoordinateList = []
        def geodeticEvaluator = new GeodeticEvaluator()
        double [] daArray = new double[3]

        try{
            geom = Geometry.fromWKT(cmd.wkt);
        }
        catch(def e)
        {
            geom = null
            result.status = HttpStatus.INTERNAL_SERVER_ERROR
            result.statusMessage = e.toString()
        }
        if(geom)
        {
            distance = 0.0;
            gdist = 0.0;
            area = 0.0;
            def imageSpaceModel = new ImageModel()
            def mapProjection

            String geomType = geom.geometryType.toUpperCase()


            println "GEOMETRY TYPE === ${geom.geometryType}"
            println "N GEOMETRIES  === ${geom.numGeometries}"
            if( (geomType != "POLYGON") && (geomType != "LINESTRING"))
            {
                result.status = HttpStatus.BAD_REQUEST
                result.statusMessage = "We only support POLYGON or LINESTRING WKT definitions for measuring distances"
            }
            else if( imageSpaceModel.setModelFromFile(cmd.filename, cmd.entryId) )
            {
                def coordinates = geom.getCoordinates();

                coordinates.each{pt->
                    imagePoint.x = pt.x;
                    imagePoint.y = pt.y;
                    if(!mapProjection)
                    {
                        Double tempX = pt.x
                        Double tempY = pt.y
                        def centroid = geom.centroid

                        imagePoint.x = centroid.x
                        imagePoint.y = centroid.y
                        mapProjection = new MapProjection();
                        imageSpaceModel.imageToGround(imagePoint, groundPoint);
                        mapProjection.createUtmProjection(groundPoint);
                        //mapProjection.createEpsgProjection(groundPoint, "EPSG:3857");
                        imagePoint.x = tempX
                        imagePoint.y = tempY
                    }
                    imageSpaceModel.imageToGround(imagePoint, groundPoint);
                    if(lastGroundPoint)
                    {
                        // Linear distance
                        distance += lastGroundPoint.distanceTo(groundPoint);

                        // Geodetic distance & azimuth
                        geodeticEvaluator.computeEllipsoidalDistAz(lastGroundPoint, groundPoint, daArray)
                        gdist += daArray[0]

                        lastGroundPoint.assign(groundPoint);
                        ecefPoint.assign(groundPoint);
                        coordinateList << [ecefPoint.x, ecefPoint.y, ecefPoint.z];

                        mapProjection.worldToLocal(groundPoint, imagePoint)
                        utmCoordinateList << [imagePoint.x, imagePoint.y]
                    }
                    else
                    {
                        lastGroundPoint = new ossimGpt(groundPoint);
                        ecefPoint.assign(lastGroundPoint);
                        coordinateList << [ecefPoint.x, ecefPoint.y, ecefPoint.z];
                        mapProjection.worldToLocal(lastGroundPoint, imagePoint)
                        utmCoordinateList << [imagePoint.x, imagePoint.y]
                    }
                }

                // Add area calculations
                if(geom instanceof geoscript.geom.Polygon)
                {
                    // make sure we complete the loop
                    utmCoordinateList << utmCoordinateList[0]

                    def tempPoly = new geoscript.geom.Polygon([utmCoordinateList])

                    //def tempPoly = new geoscript.geom.Polygon([coordinateList])
                    //def tempPoly2 = new geoscript.geom.Polygon([utmCoordinateList])

                    //println "original area: ${tempPoly.area}  NEW Area: ${tempPoly2.area}"
                    area = tempPoly.area;
                    result.data.area = area
                }

                result.data.gdist = gdist
                result.data.distance = distance
                if(coordinates.size() == 2) result.data.azimuth = Math.toDegrees(daArray[1])
            }
            imageSpaceModel.delete()
            mapProjection?.delete()
            mapProjection = null
            imageSpaceModel = null
        }
        imagePoint.delete()
        groundPoint.delete()
        ecefPoint.delete()
        geodeticEvaluator.delete()
        if(lastGroundPoint) lastGroundPoint.delete();

        result
    }
    def imagePointsToGround(IptsToGrdCommand cmd)
    {
        HashMap result = [status:HttpStatus.OK, statusMessage: ""]
        if(cmd.ipts instanceof String)
        {
          try{
              def geom = Geometry.fromWKT(cmd.ipts);
              if(geom)
              {
                  def coordinates = geom.coordinates;
                  cmd.ipts = []
                  coordinates.each{pt->
                      cmd.ipts << [x:pt.x,y:pt.y]
                  }
              }
          }
          catch(e)
          {
              result.statusMessage = e.toString()
              result.status = HttpStatus.INTERNAL_SERVER_ERROR
          }
        }
        if(result.status == HttpStatus.OK)
        {
            result = imageGeometryService.iptsToGrd(cmd)
        }

        result
    }
}
