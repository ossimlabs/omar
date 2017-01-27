//= require jquery-2.2.0.min.js
//= require webjars/openlayers/3.20.1/ol.js
//= require_self

var MapView = (function ()
{
    function init( params )
    {
        var wmsLayerUrl = '/ogc/wms';
        var footprintsLayerUrl = '/wms/footprints';

        if ( params.contextPath )
        {
            wmsLayerUrl = params.contextPath + wmsLayerUrl;
            footprintsLayerUrl = params.contextPath + footprintsLayerUrl;
        }

        var layers = [
            new ol.layer.Tile( {
                source: new ol.source.TileWMS( {
                    url: params.baseLayer.url,
                    params: {
                        'LAYERS': params.baseLayer.layers,
                        'FORMAT': params.baseLayer.format
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
    }

    return {
        init: init
    };
})();
