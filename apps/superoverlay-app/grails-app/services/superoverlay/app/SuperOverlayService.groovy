package superoverlay.app

import groovy.xml.StreamingMarkupBuilder

class SuperOverlayService
{
  static transactional = false

  def serviceMethod()
  {
    def data = [
        name: 'SuperOverlay: MV DOQQ',
        snippet: 'The original is a 7008 x 6720 DOQQ of Mountain View in 1991. (Source: http://gis.ca.gov/).',
        description: """
      The original is a 7008 x 6720 DOQQ of Mountain View in 1991. (Source:
      http://gis.ca.gov/). This is a Region NetworkLink hierarchy of 900+
      GroundOverlays each of 256 x 256 pixels arranged in a hierarchy such
      that more detailed images are loaded and shown as the viewpoint nears.
      Enable the &quot;Boxes&quot; NetworkLink to see a LineString for
      each Region. Tour the &quot;Boxes&quot; to visit each tile. Visit the
      &quot;A&quot; and &quot;B&quot; Placemarks to see multiple levels of
      hierarchy together. Use the slider on various images in the hierarchy
      to see how the resolution varies and of the entire hierarchy to see
      the imagery below. (Find the Google Campus...).
    """,
        lookAt: [
            longitude: -122.096,
            latitude: 37.401,
            range: 17000
        ],
        region: [
            bbox: [
                minX: -122.16796875,
                minY: 37.265625,
                maxX: -121.9921875,
                maxY: 37.44140625
            ]
        ],
        link: 'http://mw1.google.com/mw-earth-vectordb/kml-samples/mv-070501/1.kml'
    ]

    def x = {
      mkp.xmlDeclaration()
      kml( xmlns: 'http://earth.google.com/kml/2.1' ) {
        Document {
          name( data.name )
          Snippet( data.snippet )
          description( data.description )
          LookAt {
            longitude( data.lookAt.longitude )
            latitude( data.lookAt.latitude )
            range( data.lookAt.range )
          }
          NetworkLink {
            open( 1 )
            Style {
              ListStyle {
                listItemType( 'checkHideChildren' )
              }
            }
            Region {
              LatLonAltBox {
                north( data.region.bbox.maxY )
                south( data.region.bbox.minY )
                east( data.region.bbox.maxX )
                west( data.region.bbox.minX )
              }
              Lod {
                minLodPixels( 128 )
                maxLodPixels( -1 )
              }
            }
            Link {
              href( data.link )
              viewRefreshMode( 'onRegion' )
            }
          }
/*
    <NetworkLink>
      <name>Boxes</name>
      <visibility>0</visibility>
      <Link>
        <href>http://mw1.google.com/mw-earth-vectordb/kml-samples/mv-070501/qidboxes.kml</href>
      </Link>
    </NetworkLink>
    <Placemark>
      <name>A</name>
      <visibility>0</visibility>
      <LookAt>
        <longitude>-122.1198115439602</longitude>
        <latitude>37.38327509913652</latitude>
        <altitude>0</altitude>
        <range>3724.326998450705</range>
        <tilt>59.5501297365467</tilt>
        <heading>40.22980991388826</heading>
      </LookAt>
      <Point>
        <coordinates>-122.1198115439602,37.38327509913652,0</coordinates>
      </Point>
    </Placemark>
    <Placemark>
      <name>B</name>
      <visibility>0</visibility>
      <LookAt>
        <longitude>-122.0744937468166</longitude>
        <latitude>37.38095869430078</latitude>
        <altitude>0</altitude>
        <range>2271.276826196118</range>
        <tilt>78.37668049754895</tilt>
        <heading>-58.34372468495401</heading>
      </LookAt>
      <Point>
        <coordinates>-122.0744937468166,37.38095869430078,0</coordinates>
      </Point>
    </Placemark>
*/
        }
      }
    }

    def results = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()

    return results
  }
}
