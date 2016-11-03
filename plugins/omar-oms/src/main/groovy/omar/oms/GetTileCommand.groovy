package omar.oms

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by sbortman on 12/7/15.
 */
@ToString(includeNames = true)
class GetTileCommand implements Validateable
{
  int x
  int y
  int z
  int tileSize = 256
  String format
  String filename
  int entry = 0
  String bands
  String histOp
  Double brightness = 0.0
  Double contrast  = 1.0
  String sharpenMode = "none"
  String resamplerFilter = "nearest"
//  String styles
}
