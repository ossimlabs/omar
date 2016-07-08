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

  layers = [[
      queryable: "1",
      name: "spearfish",
      title: "spearfish",
      description: "Layer-Group type layer: spearfish",
      geoBounds: [
          minLon: -103.87791475407893,
          maxLon: -103.62278893469492,
          minLat: 44.37246687108142,
          maxLat: 44.50235105543566
      ],
      bounds: [
          crs: 'EPSG:26713',
          maxX: 609518.6719560538,
          maxY: 4928082.949945881,
          minX: 589425.9342365642,
          minY: 4913959.224611808
      ]

  ], [
      queryable: "1",
      name: "tasmania",
      title: "tasmania",
      description: "Layer-Group type layer: tasmania",
      geoBounds: [
          minLon: 143.83482400000003,
          maxLon: 148.47914100000003,
          minLat: -43.648056,
          maxLat: -39.573891
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: -39.573891,
          maxY: 148.47914100000003,
          minX: -43.648056,
          minY: 143.83482400000003
      ]
  ], [
      queryable: "1",
      name: "tiger-ny",
      title: "tiger-ny",
      description: "Layer-Group type layer: tiger-ny",
      geoBounds: [
          minLon: -74.047185,
          maxLon: -73.907005,
          minLat: 40.679648,
          maxLat: 40.882078
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 40.882078,
          maxY: -73.907005,
          minX: 40.679648,
          minY: -74.047185
      ]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'nurc:Arc_Sample',
      title: 'A sample ArcGrid file',
      description: '',
      keywords: [
          'WCS',
          'arcGridSample',
          'arcGridSample_Coverage',
      ],
      geoBounds: [
          minLon: -180.0,
          maxLon: 180.0,
          minLat: -90.0,
          maxLat: 90.0
      ],
      bounds: [
          crs: "EPSG:4326", maxY: 90.0, maxX: 180.0, minY: -90.0, minX: -180.0
      ],
      styles: [[
          name: 'rain',
          title: 'Rain distribution',
          legend: [
              height: 123,
              width: 130,
              format: 'image/png',
              url: 'http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=nurc:Arc_Sample'
          ]
      ], [
          name: 'raster',
          title: 'Default Raster',
          description: 'A sample style that draws a raster, good for displaying imagery',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=nurc:Arc_Sample&style=raster"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'nurc:Img_Sample',
      title: 'North America sample imagery',
      description: '',
      keywords: [
          'WCS',
          'worldImageSample',
          'worldImageSample_Coverage'
      ],
      geoBounds: [
          minLon: -130.85168,
          maxLon: -62.0054,
          minLat: 20.7052,
          maxLat: 54.1141
      ],
      bounds: [crs: "EPSG:4326", maxX: 54.1141, maxY: -62.0054, minX: 20.7052, minY: -130.85168],
      styles: [[
          name: 'raster',
          title: 'Default Raster',
          description: 'A sample style that draws a raster, good for displaying imagery',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=nurc:Img_Sample"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:archsites',
      title: 'Spearfish archeological sites',
      description: 'Sample data from GRASS, archeological sites location, Spearfish, South Dakota, USA',
      keywords: [
          'archsites',
          'spearfish',
          'sfArchsites',
          'archeology'
      ],
      geoBounds: [
          minLon: -103.8725637911543,
          maxLon: -103.63794182141925,
          minLat: 44.37740330855979,
          maxLat: 44.48804280772808
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 608346.4603107043, maxY: 4926501.8980334345,
          minX: 589851.4376666048, minY: 4914490.882968263
      ],
      styles: [[
          name: 'point',
          title: 'Default Point',
          description: 'A sample style that draws a point',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:archsites"
          ]
      ], [
          name: 'capitals',
          title: 'Capital cities',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:archsites&style=capitals"
          ]
      ], [
          name: 'burg',
          title: 'A small red flag',
          description: 'A sample of how to use an SVG based symbolizer',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:archsites&style=burg"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:bugsites',
      title: 'Spearfish bug locations',
      description: 'Sample data from GRASS, bug sites location, Spearfish, South Dakota, USA',
      keywords: [
          'spearfish',
          'sfBugsites',
          'insects',
          'bugsites',
          'tiger_beetles'
      ],
      geoBounds: [
          minLon: -103.86796131703647,
          maxLon: -103.63773523234195,
          minLat: 44.373938816704396,
          maxLat: 44.43418821380063
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 608462.4604629107,
          maxY: 4920523.89081033,
          minX: 590223.4382724703,
          minY: 4914107.882513998
      ],
      styles: [[
          name: 'capitals',
          title: 'Capital cities',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:bugsites"
          ]
      ], [
          name: 'point',
          title: 'Default Point',
          description: 'A sample style that draws a point',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:bugsites&style=point"
          ]
      ], [
          name: 'burg',
          title: 'A small red flag',
          description: 'A sample of how to use an SVG based symbolizer',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:bugsites&style=burg"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'omar:cities',
      title: 'cities',
      description: '',
      keywords: [
          'cities',
          'features'
      ],
      geoBounds: [
          minLon: -180.0,
          maxLon: 180.0,
          minLat: -90.0,
          maxLat: 90.0
      ],
      bounds: [
          crs: "EPSG:4326",
          minX: -180.0,
          maxX: 180.0,
          minY: -90.0,
          maxY: 90.0
      ],
      styles: [[
          name: 'point',
          title: 'Default Point',
          description: 'A sample style that draws a point',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=omar:cities"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'tiger:giant_polygon',
      title: 'World rectangle',
      description: "A simple rectangular polygon covering most of the world, it's only used for the purpose of providing a background (WMS bgcolor could be used instead)",
      keywords: [
          'DS_giant_polygon',
          'giant_polygon'
      ],
      geoBounds: [
          minLon: -180.0,
          maxLon: 180.0,
          minLat: -90.0,
          maxLat: 90.0
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 90.0,
          maxY: 180.0,
          minX: -90.0,
          minY: -180.0
      ],
      styles: [[
          name: 'giant_polygon',
          title: 'Border-less gray fill',
          description: 'Light gray polygon fill without a border',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:giant_polygon"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'nurc:mosaic',
      title: 'mosaic',
      description: '',
      keywords: [
          'WCS',
          'ImageMosaic',
          'mosaic'
      ],
      geoBounds: [
          minLon: 6.346,
          maxLon: 20.83,
          minLat: 36.492,
          maxLat: 46.591
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 46.591,
          maxY: 20.83,
          minX: 36.492,
          minY: 6.346
      ],
      styles: [[
          name: 'raster',
          title: 'Default Raster',
          description: 'A sample style that draws a raster, good for displaying imagery',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=nurc:mosaic"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'tiger:poi',
      title: 'Manhattan (NY) points of interest',
      description: 'Points of interest in New York, New York (on Manhattan). One of the attributes contains the name of a file with a picture of the point of interest.',
      keywords: [
          'poi',
          'Manhattan',
          'DS_poi',
          'points_of_interest'
      ],
      geoBounds: [
          minLon: -74.0118315772888,
          maxLon: -74.00857344353275,
          minLat: 40.70754683896324,
          maxLat: 40.711945649065406
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 40.719885123828675,
          maxY: -74.00153046439813,
          minX: 40.70754683896324,
          minY: -74.0118315772888
      ],
      styles: [[
          name: 'poi',
          title: 'Points of interest',
          description: 'Manhattan points of interest',
          legend: [
              height: 40,
              width: 22,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poi"
          ]
      ], [
          name: 'point',
          title: 'Default Point',
          description: 'A sample style that draws a point',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poi&style=point"
          ]
      ], [
          name: 'burg',
          title: 'A small red flag',
          description: 'A sample of how to use an SVG based symbolizer',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poi&style=burg"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'tiger:poly_landmarks',
      title: 'Manhattan (NY) landmarks',
      description: 'Manhattan landmarks, identifies water, lakes, parks, interesting buildilngs',
      keywords: [
          'landmarks',
          'DS_poly_landmarks',
          'manhattan',
          'poly_landmarks'
      ],
      geoBounds: [
          minLon: -74.047185,
          maxLon: -73.90782,
          minLat: 40.679648,
          maxLat: 40.882078
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 40.882078,
          maxY: -73.90782,
          minX: 40.679648,
          minY: -74.047185
      ],
      styles: [[
          name: 'poly_landmarks',
          legend: [
              height: 80,
              width: 22,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poly_landmarks"
          ]
      ], [
          name: 'grass',
          title: 'Grass fill',
          description: 'A style filling polygons with a grass theme coming from a PNG file',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger/poly_landmarks&style=grass"
          ]
      ], [
          name: 'polygon',
          title: 'Default Polygon',
          description: 'A sample style that draws a polygon',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poly_landmarks&style=polygon"
          ]
      ], [
          name: 'restricted',
          title: 'Red, translucent style',
          description: 'A sample style that just prints out a transparent red interior with a red outline',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:poly_landmarks&style=restricted"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'omar:raster_entry',
      title: 'raster_entry',
      description: '',
      keywords: [
          'raster_entry',
          'features'
      ],
      geoBounds: [
          minLon: -122.789247030861,
          maxLon: 55.0222636010388,
          minLat: -1.0E-16,
          maxLat: 41.5350811298077
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 41.5350811298077,
          maxY: 55.0222636010388,
          minX: -1.0E-16,
          minY: -122.789247030861
      ],
      styles: [[
          name: 'polygon',
          title: 'Default Polygon',
          description: 'A sample style that draws a polygon',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=omar:raster_entry"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:restricted',
      title: 'Spearfish restricted areas',
      description: 'Sample data from GRASS, restricted areas, Spearfish, South Dakota, USA',
      keywords: [
          'spearfish',
          'restricted',
          'areas',
          'sfRestricted'
      ],
      geoBounds: [
          minLon: -103.85057172920756,
          maxLon: -103.74741494853805,
          minLat: 44.39436387625042,
          maxLat: 44.48215752041131
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 599648.9251686076,
          maxY: 4925872.146218054,
          minX: 591579.1858092896,
          minY: 4916236.662227167
      ],
      styles: [[
          name: 'restricted',
          title: 'Red, translucent style',
          description: 'A sample style that just prints out a transparent red interior with a red outline',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:restricted"
          ]
      ], [
          name: 'polygon',
          title: 'Default Polygon',
          description: 'A sample style that draws a polygon',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:restricted&style=polygon"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:roads',
      title: 'Spearfish roads',
      description: 'Sample data from GRASS, road layout, Spearfish, South Dakota, USA',
      keywords: [
          'sfRoads',
          'spearfish',
          'roads'
      ],
      geoBounds: [
          minLon: -103.87741691493184,
          maxLon: -103.62231404880659,
          minLat: 44.37087275281798,
          maxLat: 44.50015918338962
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 609527.2102150217,
          maxY: 4928063.398014731,
          minX: 589434.8564686741,
          minY: 4914006.337837095
      ],
      styles: [[
          name: 'simple_roads',
          title: 'Default Styler for simple road segments',
          description: 'Light red line, 2px wide',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:roads"
          ]
      ], [
          name: 'line',
          title: 'Default Line',
          description: 'A sample style that draws a line',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:roads&style=line"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:sfdem',
      title: 'sfdem is a Tagged Image File Format with Geographic information',
      description: '',
      keywords: [
          'WCS',
          'sfdem',
          'sfdem'
      ],
      geoBounds: [
          minLon: -103.87108701853181,
          maxLon: -103.62940739432703,
          minLat: 44.370187074132616,
          maxLat: 44.5016011535299
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 609000.0,
          maxY: 4928010.0,
          minX: 589980.0,
          minY: 4913700.0
      ],
      styles: [[
          name: 'dem',
          title: 'Simple DEM style',
          description: 'Classic elevation color progression',
          legend: [
              height: 212,
              width: 80,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:sfdem"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'topp:states',
      title: 'USA Population',
      description: 'This is some census data on the states.',
      keywords: [
          'census',
          'united',
          'boundaries',
          'state',
          'states'
      ],
      geoBounds: [
          minLon: -124.731422,
          maxLon: -66.969849,
          minLat: 24.955967,
          maxLat: 49.371735
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: 49.371735,
          maxY: -66.969849,
          minX: 24.955967,
          minY: -124.73142200000001
      ],
      styles: [[
          name: 'population',
          title: 'Population in the United States',
          description: 'A sample filter that filters the United States into three categories of population, drawn in different colors',
          legend: [
              height: 80,
              width: 76,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:states"
          ]
      ], [
          name: 'pophatch',
          title: 'Population in the United States',
          description: 'A sample filter that filters the United States into three categories of population, drawn in different colors',
          legend: [
              height: 80,
              width: 76,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:states&style=pophatch"
          ]
      ], [
          name: 'polygon',
          title: 'Default Polygon',
          description: 'A sample style that draws a polygon',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:states&style=polygon"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'sf:streams',
      title: 'Spearfish streams',
      description: 'Sample data from GRASS, streams, Spearfish, South Dakota, USA',
      keywords: [
          'spearfish',
          'sfStreams',
          'streams'
      ],
      geoBounds: [
          minLon: -103.87789019829768,
          maxLon: -103.62287788915457,
          minLat: 44.372335260095554,
          maxLat: 44.502218486214815
      ],
      bounds: [
          crs: "EPSG:26713",
          maxX: 609518.2117427464,
          maxY: 4928071.049965891,
          minX: 589434.4971235897,
          minY: 4913947.342298816
      ],
      styles: [[
          name: 'simple_streams',
          title: 'Default Styler for streams segments',
          description: 'Blue lines, 2px wide',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:streams"
          ]
      ], [
          name: 'line',
          title: 'Default Line',
          description: 'A sample style that draws a line',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=sf:streams&style=line"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'topp:tasmania_cities',
      title: 'Tasmania cities',
      description: 'Cities in Tasmania (actually, just the capital)',
      keywords: [
          'cities',
          'Tasmania'
      ],
      geoBounds: [
          minLon: 145.19754,
          maxLon: 148.27298000000002,
          minLat: -43.423512,
          maxLat: -40.852802
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: -40.852802,
          maxY: 148.27298000000002,
          minX: -43.423512,
          minY: 145.19754
      ],
      styles: [[
          name: 'capitals',
          title: 'Capital cities',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:tasmania_cities"
          ]
      ]]
  ], [
      opaque: "0",
      queryable: "1",
      name: 'topp:tasmania_roads',
      title: 'Tasmania roads',
      description: 'Main Tasmania roads',
      keywords: [
          'Roads',
          'Tasmania'
      ],
      geoBounds: [
          minLon: 145.19754,
          maxLon: 148.27298000000002,
          minLat: -43.423512,
          maxLat: -40.852802
      ],
      bounds: [
          crs: "EPSG:4326",
          maxY: -40.852802,
          maxX: 148.27298000000002,
          minY: -43.423512,
          minX: 145.19754
      ],
      styles: [[
          name: 'simple_roads',
          title: 'Default Styler for simple road segments',
          description: 'Light red line, 2px wide',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:tasmania_roads"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'topp:tasmania_state_boundaries',
      title: 'Tasmania state boundaries',
      description: 'Tasmania state boundaries',
      keywords: [
          'boundaries',
          'tasmania_state_boundaries',
          'Tasmania'
      ],
      geoBounds: [
          minLon: 143.83482400000003,
          maxLon: 148.47914100000003,
          minLat: -43.648056,
          maxLat: -39.573891
      ],
      bounds: [
          crs: "EPSG:4326",
          maxX: -39.573891,
          maxY: 148.47914100000003,
          minX: -43.648056,
          minY: 143.83482400000003
      ],
      styles: [[
          name: 'green',
          title: 'Green polygon',
          description: 'Green fill with black outline',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:tasmania_state_boundaries"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'topp:tasmania_water_bodies',
      title: 'Tasmania water bodies',
      description: 'Tasmania water bodies',
      keywords: [
          'Lakes',
          'Bodies',
          'Australia',
          'Water',
          'Tasmania'
      ],
      geoBounds: [
          minLon: 145.97161899999998,
          maxLon: 147.219696,
          minLat: -43.031944,
          maxLat: -41.775558
      ],
      bounds: [
          crs: "EPSG:4326",
          maxY: -41.775558,
          maxX: 147.219696,
          minY: -43.031944,
          minX: 145.97161899999998
      ],
      styles: [[
          name: 'cite_lakes',
          title: 'Blue lake',
          description: 'A blue fill, solid black outline style',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=topp:tasmania_water_bodies"
          ]
      ]]

  ], [
      opaque: "0",
      queryable: "1",
      name: 'tiger:tiger_roads',
      title: 'Manhattan (NY) roads',
      description: 'Highly simplified road layout of Manhattan in New York..',
      keywords: [
          'DS_tiger_roads',
          'tiger_roads',
          'roads'
      ],
      geoBounds: [
          minLon: -74.02722,
          maxLon: -73.907005,
          minLat: 40.684221,
          maxLat: 40.878178
      ],
      bounds: [
          crs: "EPSG:4326",
          maxY: "40.878178",
          maxX: -73.907005,
          minY: 40.684221,
          minX: -74.02722
      ],
      styles: [[
          name: 'tiger_roads',
          legend: [
              height: 80,
              width: 22,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:tiger_roads"
          ]
      ], [
          name: 'simple_roads',
          title: 'Default Styler for simple road segments',
          description: 'Light red line, 2px wide',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:tiger_roads&style=simple_roads"
          ]
      ], [
          name: 'line',
          title: 'Default Line',
          description: 'A sample style that draws a line',
          legend: [
              height: 20,
              width: 20,
              format: 'image/png',
              url: "http://localhost:8080/geoserver/ows?service=WMS&request=GetLegendGraphic&format=image/png&width=20&height=20&layer=tiger:tiger_roads&style=line"
          ]
      ]]
  ]]
}
