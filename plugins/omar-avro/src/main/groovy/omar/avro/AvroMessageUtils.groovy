package omar.avro
import org.joda.time.DateTime

import org.joda.time.chrono.ISOChronology
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.DateTimeZone
import org.apache.commons.io.FilenameUtils
import groovy.util.logging.Slf4j

@Slf4j
class AvroMessageUtils
{
  static String getDestinationSuffixFromMessage(def jsonObj)
  {
    String result
    String sourceUri = jsonObj."${OmarAvroUtils.avroConfig.sourceUriField}"
    String dateStringFormat = "${OmarAvroUtils.avroConfig.dateFieldFormat}"
    if(jsonObj."${OmarAvroUtils.avroConfig.dateField}"&&
       jsonObj."${OmarAvroUtils.avroConfig.imageIdField}")
    {
      String dateString    = jsonObj."${OmarAvroUtils.avroConfig.dateField}"
      String imageIdString = jsonObj."${OmarAvroUtils.avroConfig.imageIdField}"

      try{
        DateTimeFormatter formatter = DateTimeFormat.forPattern(dateStringFormat)
                    .withLocale(Locale.ROOT)
                    .withChronology(ISOChronology.getInstanceUTC());

        DateTime dt = formatter.parseDateTime(dateString);
        DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy/MM/dd/HH")

        String datePart = formatter2.withZone(DateTimeZone.UTC).print(dt)
        String imageId = jsonObj.Image_Id
        result = new File(datePart, imageId).toString()

        String ext = FilenameUtils.getExtension(new URL(sourceUri).path.toString())
        if(ext)
        {
          result = "${result}.${ext}"
        }

      }
      catch(e)
      {
        log.error "Unable to convert date: ${dateString} with format ${OmarAvroUtils.avroConfig.dateFieldFormat}"
      }
    }
    else if(sourceUri)
    {
      result = new URL(sourceUri).path.toString()
    }
    result
  }
  static Boolean tryToCreateDirectory(File directory, HashMap config)
  {
      Integer numberOfAttempts = config?.numberOfAttempts?:3
      Integer sleepInMillis    = config?.sleepInMills?:100
      Boolean result = false
      Integer attempt = 0
      if(directory.exists()) return true

      while((attempt < numberOfAttempts)&&!result)
      {
         directory.mkdirs()
         if(directory.exists())
         {
            result = true
         }
         else
         {
            sleep(sleepInMillis)

            ++attempt
         }
      }
      
      result
  }
}