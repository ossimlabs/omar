//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.20.1/ol.js
//= require_self

var MapView = (function ()
{
    var postDoc = ' \
<wfs:GetFeature service="WFS" version="1.0.0" \
        resultType="hits" \
        outputFormat="GML2" \
        xmlns:omar="http://omar.ossim.org" \
        xmlns:wfs="http://www.opengis.net/wfs" \
        xmlns:ogc="http://www.opengis.net/ogc"  \
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
        xsi:schemaLocation="http://www.opengis.net/wfs \
        http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"> \
    <wfs:Query srsName="EPSG:4326" typeName="omar:raster_entry"> \
        <ogc:Filter xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc"> \
            <ogc:Intersects> \
                <ogc:PropertyName>ground_geom</ogc:PropertyName> \
                <gml:Polygon> \
                    <gml:outerBoundaryIs> \
                        <gml:LinearRing> \
                            <gml:coordinates>XXXXXX</gml:coordinates> \
                        </gml:LinearRing> \
                    </gml:outerBoundaryIs> \
                </gml:Polygon> \
            </ogc:Intersects> \
        </ogc:Filter> \
    </wfs:Query> \
</wfs:GetFeature>';

    var wfsUrl = '/wfs';

    function init( params )
    {
        var wmsLayerUrl = '/ogc/wms';
        var footprintsLayerUrl = '/wms/footprints';

        if ( params.contextPath )
        {
            wmsLayerUrl = params.contextPath + wmsLayerUrl;
            footprintsLayerUrl = params.contextPath + footprintsLayerUrl;
            wfsUrl = params.contextPath + wfsUrl;
        }

        var layers = [
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: params.baseLayer.url,
                    params: {
                        'LAYERS': params.baseLayer.layers,
                        'FORMAT': params.baseLayer.format,
                    }
                } )
            } ),
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: wmsLayerUrl,
                    params: {
                        'VERSION': '1.1.1',
                        'LAYERS': params.wmsLayers
                    }
                } )
            } ),
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: footprintsLayerUrl,
                    params: {
                        'VERSION': '1.1.1',
                        'LAYERS': 'Imagery',
                        'STYLES': 'green',
                        'FORMAT': params.footprintsFormat,
                        'TIME': params.footprintsTime
                    }
                } )
            } )
        ];

        var map = new ol.Map( {
            controls: ol.control.defaults().extend( [
                new ol.control.ScaleLine( {
                    units: 'degrees'
                } )
            ] ),
            layers: layers,
            target: 'map',
            view: new ol.View( {
                projection: 'EPSG:4326',
                center: [0, 0],
                zoom: 2
            } )
        } );

        map.on( 'moveend', onMoveEnd )
    }

    function onMoveEnd( evt )
    {
        var map = evt.map;
        var extent = map.getView().calculateExtent( map.getSize() );

        // console.log( extent );

        var minX = extent[0];
        var minY = extent[1];
        var maxX = extent[2];
        var maxY = extent[3];

        var coords = [
            [minX, minY], [minX, maxY], [maxX, maxY], [maxX, minY], [minX, minY]
        ].join( ' ' );

        var newPostDoc = postDoc.replace( 'XXXXXX', coords );

        // console.log( newPostDoc );

        $.ajax( {
            url: wfsUrl,
            data: newPostDoc,
            type: 'POST',
            contentType: "text/xml",
            dataType: "text",
            success: getFeature,
            error: function ( xhr, ajaxOptions, thrownError )
            {
                console.log( xhr.status );
                console.log( thrownError );
            }
        } );
    }

    function getFeature( results )
    {
        var $features = $( $.parseXML( results ) );
        var count = $features.find( 'FeatureCollection' ).attr( 'numberOfFeatures' );
        console.log( JSON.stringify( {numberOfFeatures: count} ) );
    }

    return {
        init: init
    };
})();
