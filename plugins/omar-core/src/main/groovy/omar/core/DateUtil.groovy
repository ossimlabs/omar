package omar.core

import java.math.MathContext
import java.text.SimpleDateFormat

import javax.servlet.http.HttpServletRequest

import org.joda.time.format.ISOPeriodFormat
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.DateTimeFormatter

//import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 16, 2008
 * Time: 1:58:06 PM
 * To change this template use File | Settings | File Templates.
 */

class DateUtil
{

  static def createDateBetweenYears(def startYear, def endYear)
  {
    def rng = new Random()
    def year = startYear + rng.nextInt(endYear - startYear + 1)
    def month = rng.nextInt(12)

    def calendar = new GregorianCalendar(year, month, 1);
    def day = rng.nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1)

    def hour = rng.nextInt(24)
    def minute = rng.nextInt(60)
    def second = rng.nextInt(60)
    def millisecond = rng.nextInt(1000)

    def date = Date.parse("yyyy-MM-dd HH:mm:ss.SSS",
            "${year}-${month}-${day} ${hour}:${minute}:${second}.${millisecond}")

    return date
  }


  public static Date parseDate(String dateString)
  {
    TimeZone utc = null
    SimpleDateFormat sdf = null
    Date date = null
    switch ( dateString )
    {
      case ~/[0-9]{4}/:
        sdf = new SimpleDateFormat("yyyy");
//        println "one: ${dateString}"
        break
      case ~/[0-9]{4}[0-1][0-9][0-3][0-9]/:
        sdf = new SimpleDateFormat("yyyyMMdd");
//        println "one: ${dateString}"
        break
    case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]/:
      sdf = new SimpleDateFormat("yyyy-MM-dd");
      //println "one: ${dateString}"
      break
    case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z/:
      utc = TimeZone.getTimeZone("UTC");
      sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      //println "two: ${dateString}"
      break
    case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}Z/:
      utc = TimeZone.getTimeZone("UTC");
      sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      //println "three: ${dateString}"
      break
    case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5]?[0-9].[0-9]{1,}Z/:
      def x = dateString.split('T')
      def y = x[0].split('-')
      def z = (x[1] - 'Z').split(':')
      def r = new BigDecimal(z[2]).round(new MathContext(5)) as String

      if ( r.size() == 1 )
      r = "00.000"

      z[2] = r
      dateString = "${y[0]}-${y[1]}-${y[2]}T${z[0]}:${z[1]}:${z[2]}Z"
      utc = TimeZone.getTimeZone("UTC");
      sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      //println "four: ${dateString}"
      break
    }

    if ( sdf )
    {
      if ( utc )
      {
        sdf.setTimeZone(utc);
      }

      date = sdf.parse(dateString)
    }

    return date
  }

  static def parseDateGivenFormats(String dateString, def dateFormats = null)
  {
    def date = null

    if ( !dateFormats )
    {
      dateFormats = [
              "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
              "MM'/'dd'/'yyyy HH:mm:ss",
              "yyyyMMdd'T'HH:mm:ss",
              "yyyyMMdd'T'HH:mm:ss.ssss",
              "EEE MMM dd HH:mm:ss ZZZ yyyy",
              "yyyy-MM-dd HH:mm:ss"
      ]
    }

    if ( dateString )
    {
      for ( def dateFormat in dateFormats )
      {
        try
        {
          date = Date.parse(dateFormat, dateString)

          if ( date )
          {
            break
          }
        }
        catch (Exception e)
        {
//       println "Cannot parse ${dateString}: using ${dateFormat}"
        }
      }
    }

    return date
  }

  public static Date initializeDate(String dateField, Map params)
  {
    Date date = null

    if ( params[dateField] )
    {
//      if ( params[dateField] ==~ "(date.)?struct" )
//      {
////		println "STRUCT ${params[dateField]}"
//        def paramMap = new GrailsParameterMap(params, {} as HttpServletRequest)
//        date = paramMap.getProperty(dateField)
//      }
//      else
//

      if ( params[dateField] instanceof Date )
      {
//		println "DATE ${params[dateField]}"
        date = params[dateField]
      }
      else if ( params[dateField] instanceof String )
      {
//		println "STRING ${params[dateField]}"
        date = parseDateGivenFormats(params[dateField])
      }
    }

    if ( date && params["${dateField}_timezone"] )
    {
      def tz = params["${dateField}_timezone"]
//		println "TZ ${tz} "
      date = setTimeZoneForDate(date, TimeZone.getTimeZone(tz))
    }

    return date
  }

  public static Date rollToEndOfDay(Date inputDate)
  {
    Date outputDate = null

    // Change the time portion of the date
    // to be the end of the day. 23:59:59.999
    if ( inputDate )
    {
      def cal = Calendar.instance
      cal.time = inputDate
      cal.set(Calendar.HOUR, 23)
      cal.set(Calendar.MINUTE, 59)
      cal.set(Calendar.SECOND, 59)
      cal.set(Calendar.MILLISECOND, 999)
      outputDate = cal.time
    }

    return outputDate
  }



  static SimpleDateFormat findDateFormatter(String dateString)
  {
    def formatter = null
    def format = null
    def timeZone = null

    switch ( dateString )
    {
    case ~/[0-9]{4}[0-9]{2}[0-9]{2}/:
      format = "yyyyMMdd";
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[zZ]/:
      format = "yyyy-MM-dd'T'hh:mm:ss'Z'"
      timeZone = TimeZone.getTimeZone("UTC")
      break
    case ~/[0-9]{4}[0-9]{2}[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[zZ]/:
      format = "yyyyMMdd'T'hh:mm:ss'Z'"
      timeZone = TimeZone.getTimeZone("UTC")
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}/:
      format = "yyyy-MM-dd'T'hh:mm:ss"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}/:
      format = "yyyy-MM-dd hh:mm:ss"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}/:
      format = "yyyy-MM-dd'T'hh:mm"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}/:
      format = "yyyy-MM-dd hh:mm"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}/:
      format = "yyyy-MM-dd'T'hh"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}/:
      format = "yyyy-MM-dd hh"
      break
    case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}/:
      format = "yyyy-MM-dd"
      break
    case ~/[0-9]{4}-[0-9]{2}/:
      format = "yyyy-MM"
      break
    case ~/[0-9]{4}/:
      format = "yyyy"
      break

    default:
        if(dateString.endsWith("'Z'"))
        {
          timeZone = TimeZone.getTimeZone("UTC")
        }

        format = dateString
        break
    }

    if ( format )
    {
      formatter = new SimpleDateFormat(format)

      if ( timeZone )
      {
        formatter.timeZone = timeZone
      }
    }

    return formatter
  }

  static Date setTimeZoneForDate(Date date, TimeZone timeZone)
  {
    Date dateWithTZ = null

    if ( date && timeZone )
    {
      Calendar input = Calendar.instance
      Calendar output = Calendar.getInstance(timeZone)

      input.time = date
      output.set(Calendar.YEAR, input.get(Calendar.YEAR))
      output.set(Calendar.MONTH, input.get(Calendar.MONTH))
      output.set(Calendar.DAY_OF_MONTH, input.get(Calendar.DAY_OF_MONTH))
      output.set(Calendar.HOUR_OF_DAY, input.get(Calendar.HOUR_OF_DAY))
      output.set(Calendar.MINUTE, input.get(Calendar.MINUTE))
      output.set(Calendar.SECOND, input.get(Calendar.SECOND))
      output.set(Calendar.MILLISECOND, input.get(Calendar.MILLISECOND))
      dateWithTZ = output.time
    }

    return dateWithTZ
  }


  static def getDateRange(String iso8601String)
  {
    def startEndSplit = iso8601String?.split('/')

    if ( !startEndSplit )
    {
      return false
    }
    def resultStartDate = null
    def resultEndDate = null

    def startDate = null
    def endDate = null

    def startPeriod = null
    def endPeriod = null

    if ( startEndSplit )
    {
      String start = startEndSplit[0].trim()

      startPeriod = parsePeriod(start)

      if ( !startPeriod &&start)
      {
        resultStartDate = parseDateTime(start);
      }

      if ( startEndSplit.size() > 1 )
      {
        String end = startEndSplit[1].trim()

        endPeriod = parsePeriod(end)

        if ( !endPeriod )
        {
          resultEndDate = parseDateTime(end);
          endDate = parseDateTime(end)
        }
      }
    }

    if ( resultStartDate )
    {
      if ( endPeriod )
      {
        def interval = new org.joda.time.Interval(resultStartDate, endPeriod);
        resultStartDate = interval.getStart();
        resultEndDate   = interval.getEnd();
      }
    }
    else if ( resultEndDate )
    {
      if ( startPeriod )
      {
        def interval = new org.joda.time.Interval(startPeriod, resultEndDate);
        resultStartDate = interval.getStart();
        resultEndDate   = interval.getEnd();
      }
    }
    if(resultStartDate) startDate = new Date(resultStartDate.getMillis())
    if(resultEndDate)   endDate   = new Date(resultEndDate.getMillis())

    return [startDate, endDate]
  }

  /**
   * This uses the joda time library to parse a period formatted string.
   * We will limit it to the standard ISO format - PyYmMwWdDThHmMsS
   *
   * @param iso8601Period
   * @return null if unable to produce a period and a valid period object otherwise
   *
   */
  static org.joda.time.Period parsePeriod(String iso8601Period)
  {
    org.joda.time.Period result = null
    if(!iso8601Period) return result
    PeriodFormatter periodFormatter = ISOPeriodFormat.standard()
    // Try the stadnard period format of the form
    // The standard ISO format - PyYmMwWdDThHmMsS
    try
    {
      result = periodFormatter.parsePeriod(iso8601Period)
    }
    catch(Exception e)
    {
      result = null
    }

    result
  }

  /**
   * This uses the joda time library to parse a IOS8601 date time string.  We first look for the
   * hard coded forms such as:
   * yyyyMMdd'T'HHmmss.SSSZ
   * yyyyMMdd'T'HHmmssZ
   * yyyy-MM-dd'T'HH:mm:ssZZ
   * yyyy-MM-dd'T'HH:mm:ss.SSSZZ
   * yyyyMMdd
   *
   * If they fail we allocate a generalized joida time parser that looks
   * for the following described patterns:
   *
   * datetime          = time | date-opt-time
   * time              = 'T' time-element [offset]
   * date-opt-time     = date-element ['T' [time-element] [offset]]
   * date-element      = std-date-element | ord-date-element | week-date-element
   * std-date-element  = yyyy ['-' MM ['-' dd]]
   * ord-date-element  = yyyy ['-' DDD]
   * week-date-element = xxxx '-W' ww ['-' e]
   * time-element      = HH [minute-element] | [fraction]
   * minute-element    = ':' mm [second-element] | [fraction]
   * second-element    = ':' ss [fraction]
   * fraction          = ('.' | ',') digit+
   * offset            = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
   *
   * @param iso8601DateTime
   * @return null if unable to produce a DateTime else a valid DateTime object
   *
   */
  static org.joda.time.DateTime parseDateTime(String iso8601DateTime)
  {
    org.joda.time.DateTime result = null;

    // Try the basic date time of the form
    // yyyyMMdd'T'HHmmss.SSSZ
    DateTimeFormatter formatter = org.joda.time.format.ISODateTimeFormat.basicDateTime();
    try
    {
      result = formatter.parseDateTime(iso8601DateTime);
    }
    catch (Exception e)
    {
      result = null;
    }

    if(!result)
    {
      // now lets try the form
      // yyyyMMdd'T'HHmmssZ
      formatter = org.joda.time.format.ISODateTimeFormat.basicDateTimeNoMillis();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }

    if(!result)
    {
      // now lets try the form
      // yyyy-MM-dd'T'HH:mm:ssZZ
      formatter = org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }
    if(!result)
    {
      // now lets try the form
      // yyyy-MM-dd'T'HH:mm:ss.SSSZZ
      formatter = org.joda.time.format.ISODateTimeFormat.dateTime();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }

    if(!result)
    {
      // Try to parse for format yyyyMMdd
      formatter = org.joda.time.format.ISODateTimeFormat.basicDate();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }

    // last resort we will let joda do a generic
    // date or time parsing and return the proper object
    //
    if(!result)
    {
      formatter = org.joda.time.format.ISODateTimeFormat.dateTimeParser();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }
    return result;
  }

  /**
   * Sets up a OGC time interval parser and uses the parsePeriod and parseDateTime
   * parsers to construct the intervals.  We currently do not support periodicity
   * but we do support intervals and interval lists.  For example:
   * P1Y/1999,2000/P10Y
   *
   * would produce 2 intervals the first
   * would be from jan 1 1998 through jan 1 1999
   * and the second
   * would be from january 1 2000 -  january 1 2010
   *
   * @param ogcIntervals
   * @return a list of intervals.  If the intervals were invalid or none
   *         specified an empty list would be returned.
   */
  static def parseOgcTimeStartEndPairs(String ogcIntervals)
  {
    def intervals = ogcIntervals?.split(',');
    def intervalPairResult = [];
    intervals?.each{intervalValue->
      def d1 = null;
      def d2 = null;
      def range =intervalValue.split("/");
      if(range)
      {
        String dateString = range[0].trim();
        d1 = parsePeriod(dateString);
        if(!d1)
        {
          d1 = parseDateTime(dateString);
        }
        if(range.size() > 1)
        {
          dateString =  range[1].trim();
          d2 = parsePeriod(dateString);
          if(!d2)
          {
            d2 = parseDateTime(dateString);
          }
        }
        else
        {
          d2 = d1;
        }
        if(d1||d2)
        {
          intervalPairResult << [start:d1,end:d2]
        }

        d1 = null;
        d2 = null;
      }
    }
    intervalPairResult
  }

  static def parseOgcTimeIntervals(String ogcIntervals)
  {
    def intervalPairs  = parseOgcTimeStartEndPairs(ogcIntervals)
    def intervalResult = []
    intervalPairs.each{pair->
      if(pair.start && pair.end)
      {
        try
        {
          def interval = new org.joda.time.Interval(pair.start, pair.end);
          intervalResult << interval;
        }
        catch(Exception e)
        {
        }
      }
    }

    return intervalResult;
  }

  /**
   * Sets up a OGC time interval parser and uses the parsePeriod and parseDateTime
   * parsers to construct the intervals.  We currently do not support periodicity
   * but we do support intervals and interval lists.  For example:
   * P1Y/1999,2000/P10Y
   *
   * would produce 2 intervals the first
   * would be from jan 1 1998 through jan 1 1999
   * and the second
   * would be from january 1 2000 -  january 1 2010
   *
   *
   * @param ogcIntervals comma separated list of ogc intervals
   * @param dateTimeZone optional and will convert to the specified time zone
   * must be of type org.joda.time.DateTimeZone
   * @return A list of hashs maps that will contain Joda TimeDate objects identifeid by the key start, end
    */
  static def parseOgcTimeIntervalsAsDateTime(String ogcIntervals, org.joda.time.DateTimeZone dateTimeZone=null)
  {
    def intervals  = parseOgcTimeIntervals(ogcIntervals)
    def intervalResult = []
    intervals.each{interval->
      def startEnd = [
              start:new org.joda.time.DateTime(interval.startMillis),
              end: new org.joda.time.DateTime(interval.endMillis)
      ]
      if(dateTimeZone)
      {
        startEnd.start = startEnd.start.toDateTime(dateTimeZone)
        startEnd.end = startEnd.end.toDateTime(dateTimeZone)
      }
      intervalResult << startEnd
    }

    return intervalResult;
  }

  static Date dateTimeToDate(org.joda.time.DateTime dateTime)
  {
    new Date(dateTime.millis)
  }
}
