package omar.opir

import groovy.util.slurpersupport.GPathResult
import omar.core.Repository

/**
 * Created by gpotts on 3/3/17.
 */
class OpirRasterInfoParser implements OmsInfoParser
{
   def additionalTags
   //def tagFile = new File("tags.txt")

   public def processDataSets(GPathResult oms, Repository repository = null )
   {
      def opirRasterDataSets = []

      for ( def opirRasterDataSetNode in oms?.dataSets?.OpirRasterDataSet )
      {

         OpirRasterDataSet rasterDataSet = OpirRasterDataSet.initRasterDataSet( opirRasterDataSetNode )

         if ( rasterDataSet.rasterEntries )
         {
            rasterDataSet.repository = repository
            opirRasterDataSets << rasterDataSet
            //repository?.addToRasterDataSets(rasterDataSet)
         }
      }

      return opirRasterDataSets
   }
}

