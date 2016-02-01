package omar.geoscript

class FootprintsController
{
  def footprintService

  def getFootprints(GetFootprintsRequest cmd)
  {
//    println cmd

    def results = footprintService.getFootprints( cmd )

    render contentType: results.contentType, file: results.buffer
  }
}
