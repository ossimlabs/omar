/**
 * Created by sbortman on 7/8/16.
 */
geoscript {

  serverData = [
      Service: [
          Name: "WMS",
          Title: "OMAR Web Map Service",
          Abstract: "A compliant implementation of WMS plus most of the SLD extension (dynamic styling). Can also generate PDF, SVG, KML, GeoRSS",
          KeywordList: [
              "WFS", "WMS", "OMAR"
          ],
          OnlineResource: "http://omar.ossim.org",
          ContactInformation: [
              ContactPersonPrimary: [
                  ContactPerson: "Scott Bortman",
                  ContactOrganization: "Radiant Blue Technologies, Inc."
              ],
              ContactPosition: "Code Monkey",
              ContactAddress: [
                  AddressType: "Work",
                  Address: "",
                  City: "Melbourne",
                  StateOrProvince: "FL",
                  PostCode: "32901",
                  Country: "US"
              ],
              ContactVoiceTelephone: "",
              ContactFacsimileTelephone: "",
              ContactElectronicMailAddress: "sbortman@radiantblue.com"
          ],
          Fees: "NONE",
          AccessConstraints: "NONE"
      ],
      Capability: [
          Request: [
              GetCapabilities: [
                  Format: ['1.3.0': 'text/xml',  '1.1.1': 'application/vnd.ogc.wms_xml']
              ],
              GetMap: [
                  Format: [
                      'image/png',
                      'application/atom+xml',
                      'application/pdf',
                      'application/rss+xml',
                      'application/vnd.google-earth.kml+xml',
                      'application/vnd.google-earth.kml+xml;mode=networklink',
                      'application/vnd.google-earth.kmz',
                      'image/geotiff',
                      'image/geotiff8',
                      'image/gif',
                      'image/jpeg',
                      'image/png; mode=8bit',
                      'image/svg+xml',
                      'image/tiff',
                      'image/tiff8',
                      'text/html; subtype=openlayers'
                  ]
              ],
              GetFeatureInfo: [
                  Format: [
                      'text/plain',
                      'application/vnd.ogc.gml',
                      'text/xml',
                      'application/vnd.ogc.gml/3.1.1',
                      'text/xml; subtype=gml/3.1.1',
                      'text/html',
                      'application/json'
                  ]
              ]
          ],
          Exception: [
              Format: [
                  'XML',
                  'INIMAGE',
                  'BLANK'
              ]
          ],
          Layer: [
              Title: "OMAR Web Map Service",
              Abstract: "A compliant implementation of WMS plus most of the SLD extension (dynamic styling). Can also generate PDF, SVG, KML, GeoRSS",
              CRS: [
                  'EPSG:3857',
                  'EPSG:4326'
              ],
              BoundingBox: [
                  minLon: -180.0,
                  maxLon: 180.0,
                  minLat: -90.0,
                  maxLat: 90.0,
                  crs: 'EPSG:4326'
              ]
          ]
      ]
  ]
}
