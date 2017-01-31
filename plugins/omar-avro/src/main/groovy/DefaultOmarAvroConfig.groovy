avro{
  sourceUriField="URL"
  dateField="Observation_Date_Time"
  dateFieldFormat="yyyyMMddHHmmss"
  imageIdField="Image_Id"
  createDirectoryRetry="3"
  createDirectoryRetryWaitInMillis="100"
  jsonSubFieldPath=""
  stagingDelay=0
  download{
    directory="/data"

    // if specified will replace string <source> and <destination>
    // example: wget --no-check-certificate -O <destination> "<source>"
    command=""
  }
  destination{
    type="post"
    post{
      addRasterEndPoint="http://192.168.2.200/stager-app/dataManager/addRaster"
      addRasterEndPointField="filename"
      addRasterEndPointParams{
        background="true"
        buildHistograms="true"
        buildOverviews="true"
        overviewCompressionType="NONE"
        overviewType="ossim_tiff_box"
        filename=""
      }
    }
  }
}