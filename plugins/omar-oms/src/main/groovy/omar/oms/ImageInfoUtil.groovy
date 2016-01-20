package omar.oms

import joms.oms.Info
import joms.oms.Keywordlist

/**
 * Created by sbortman on 1/15/16.
 */
class ImageInfoUtil
{
  static Map<String, String> getImageInfoAsMap(File file)
  {
    def kwl = new Keywordlist()
    def info = new Info()

    info.getImageInfo( file.absolutePath, true, true, true, true, true, true, kwl )

    def data = [:]

    for ( def i = kwl.iterator; !i.end(); )
    {
      //println "${i.key}: ${i.value}"

      def names = i.key.split( '\\.' )
      def prev = data
      def cur = data

      for ( def name in names[0..<-1] )
      {
        if ( !prev.containsKey( name ) )
        {
          prev[name] = [:]
        }

        cur = prev[name]
        prev = cur
      }
      cur[names[-1]] = i.value.trim()
      i.next()
    }

    kwl.delete()
    info.delete()

    return data
  }
}
