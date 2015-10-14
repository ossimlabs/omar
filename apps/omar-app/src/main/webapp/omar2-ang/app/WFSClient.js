/**
 * Created by sbortman on 9/28/15.
 */
//= require jquery.js
//= require OpenLayersLite-formats.js
//= require_self

'use strict';
var OGC = OGC || {WFS: {}};

OGC.WFS.Client = OpenLayers.Class({
    initialize: function ( wfsServer ){
        this.wfsServer = wfsServer;
        //this.wfsFeatureTypes = this.getFeatureTypes();
        this.wfsFeatureTypeSchemas = null; // = this.getFeatureTypeSchema();
    },
    getFeatureTypes: function (cb){
        var localFeatureTypes = [];
        var isAsync = (cb instanceof Function);

        if ( this.wfsFeatureTypes === undefined ){
            OpenLayers.Format.WFSCapabilities.v1.prototype.readers = {
                "wfs": {
                    "WFS_Capabilities": function ( node, obj )
                    {
                        this.readChildNodes( node, obj );
                    },
                    "FeatureTypeList": function ( node, request )
                    {
                        request.featureTypeList = {
                            featureTypes: []
                        };
                        this.readChildNodes( node, request.featureTypeList );
                    },
                    "FeatureType": function ( node, featureTypeList )
                    {
                        var featureType = {};
                        this.readChildNodes( node, featureType );
                        featureTypeList.featureTypes.push( featureType );
                    },
                    "Name": function ( node, obj )
                    {
                        var name = this.getChildValue( node );
                        if ( name )
                        {
                            var parts = name.split( ":" );
                            obj.name = name; //parts.pop();
                            if ( parts.length > 0 )
                            {
                                obj.featureNS = this.lookupNamespaceURI( node, parts[0] );
                            }
                        }
                    },
                    "Title": function ( node, obj )
                    {
                        var title = this.getChildValue( node );
                        if ( title )
                        {
                            obj.title = title;
                        }
                    },
                    "Abstract": function ( node, obj )
                    {
                        var abst = this.getChildValue( node );
                        if ( abst )
                        {
                            obj["abstract"] = abst;
                        }
                    }
                }
            };

            var formatter = new OpenLayers.Format.WFSCapabilities();

            var that = this;

            var params = {
                service: 'WFS',
                version: '1.1.0',
                request: 'GetCapabilities'
            };

            OpenLayers.Request.GET( {
                url: this.wfsServer,
                async: isAsync,
                params: params,
                success: function ( request )
                {
                    console.log('request########', request)
                    var doc = request.responseXML;
                    if ( !doc || !doc.documentElement )
                    {
                        doc = request.responseText;
                    }

                    // use the tool to parse the data
                    var response = (formatter.read( doc ));

                    //console.log( 'formatter', formatter );
                    //console.log( 'namespaces', formatter.namespaces );
                    //console.log( 'namespaceAlias', formatter.namespaceAlias );

                    //console.log('response', response);

                    // this object contains all the GetCapabilities data
                    //var capability = response.capability;

                    // I want a list of names to use in my queries
                    for ( var i = 0; i < response.featureTypeList.featureTypes.length; i++ )
                    {
                        var featureType = response.featureTypeList.featureTypes[i];

                        //                    console.log( 'featureType', featureType );

                        localFeatureTypes.push( featureType );

                        //console.log(formatter.namespaceAlias);

                        //console.log('fullName', formatter.namespaceAlias[featureType.featureNS] + ':' + featureType.name)
                    }

                    //console.log( featureTypeNames );
                    //return featureTypeNames;

                }
            } );
            that.featureTypes = localFeatureTypes;

        }
        else
        {

            //console.log( 'cached...' );

        }

        if ( isAsync )
        {
            //console.log( 'We are ASync!' );
            cb( this.featureTypes );
        }
        //console.log( isAsync );

        return this.featureTypes;
    },
    getFeatureTypeSchema: function ( featureTypeName, namespace, callback ){
        var formatter2 = new OpenLayers.Format.WFSDescribeFeatureType();
        var parts = featureTypeName.split( ":" );
        var typeName = parts.pop();
        var prefix;

        if ( parts.length > 0 )
        {
            prefix = parts.pop();
        }
        else
        {
            prefix = 'ns1';
        }

        var params = {
            service: 'WFS',
            version: '1.1.0',
            request: 'DescribeFeatureType',
            typeName: prefix + ':' + typeName,
            namespace: 'xmlns(' + prefix + '=' + namespace + ')'
        };

        var isAsync = (callback instanceof Function);
        var results;

        console.log('DescribeFeatureType Params ######', params);

        OpenLayers.Request.GET( {
            url: this.wfsServer,
            params: params,
            //dataType: "html",
            async: isAsync,
            success: function ( request )
            {
                var doc = request.responseXML;
                if ( !doc || !doc.documentElement )
                {
                    doc = request.responseText;
                }

                // use the tool to parse the data
                var response = (formatter2.read( doc ));

                //console.log( 'response', response );
                results = response;
            },
            error: function ( error )
            {
                console.log( 'error', error );
            }
        } );

        return results;
    },
    getFeature: function (featureTypeName, namespace, outputFormat, filter, callback){
        
        var parts = featureTypeName.split( ":" );
        var typeName = parts.pop();
        var prefix,
            isAsync,
            format;

        if(parts.length > 0){
            prefix = parts.pop();
        }
        else{
            prefix = 'ns1';
        }

        isAsync = (callback instanceof Function);

        // WFS getFeature request parameters
        var params = {
            service: 'WFS', // static
            version: '1.1.0', // static
            request: 'GetFeature', // static
            maxFeatures: '50', // variable?
            typeName: prefix + ':' + typeName,
            namespace: 'xmlns(' + prefix + '=' + namespace + ')',
            outputFormat: '', // variable
            filter: filter || undefined // variable
        };

        params.outputFormat = outputFormat;
        console.log('params', params);

        switch(params.outputFormat){
            case 'GML3':
                console.log('Requesting GML3');
                format = new OpenLayers.Format.GML.v3();
                break;
            case 'GML2':
                console.log('Requesting GML2');
                format = new OpenLayers.Format.GML.v2();
                break;
            case 'JSON':
                console.log('Requesting JSON');
                format = new OpenLayers.Format.JSON();
            default:
                console.log('Default');
                break;
        }

        OpenLayers.Request.GET({
            url: this.wfsServer,
            params: params,
            success: function (request){
                var response = request;

                var doc = request.responseXML;

                if (!doc || !doc.documentElement ){
                    doc = request.responseText;
                }

                // use the formatter to parse the data
                var response = (format.read(doc));

                console.log(response.features);

                if (isAsync){
                    callback(response.features);
                }
            },
            failure: function (error){
                alert(error);
            }
        } );
    },

    convertCqlToXml: function (filterCql){
        var cql = new OpenLayers.Format.CQL();
        var xml = new OpenLayers.Format.XML();
        var filterXml = xml.write( new OpenLayers.Format.Filter( {version: "1.1.0"} ).write( cql.read( filterCql ) ) );

        return filterXml;
    },


    CLASS_NAME: "OGC.WFS.Client"
} );

var WFSClient = (function ()
{
    function getName( node, obj )
    {
        var name = this.getChildValue( node );
        if ( name )
        {
            var parts = name.split( ":" );
            obj.name = name; // parts.pop();
            if ( parts.length > 0 )
            {
                obj.featureNS = this.lookupNamespaceURI( node, parts[0] );
            }
        }
    }

    var wfsVersions = ['v1', 'v1_0_0', 'v1_1_0', 'v2_0_0'];

    for ( var i = 0; i < wfsVersions.length; i++ )
    {
        OpenLayers.Util.extend( OpenLayers.Format.WFSCapabilities[wfsVersions[i]].prototype.readers.wfs, {
            Name: getName
        } );
    }


    function init( params )
    {
        if ( params.wfsProxy ) {
            console.log(params.wfsProxy);
            OpenLayers.ProxyHost = params.wfsProxy;
        }


        var wfsClient = new OGC.WFS.Client( params.wfsServer );

        console.log( 'getFeatureTypes', wfsClient.getFeatureTypes() );

        /*
        console.log( 'getFeatureTypeSchema for topp:states',
            wfsClient.getFeatureTypeSchema( 'states', 'http://www.openplans.org/topp' ) );

        wfsClient.getFeature(
            'states',
            'http://www.openplans.org/topp',
            "STATE_ABBR='IN'",
            function ( it )
            {
                console.log( "getFeature for topp:states where STATE_ABBR='IN'", it );
            }
        );
        */

        //wfsClient.getFeature('raster_entry', 'http://omar.ossim.org', "file_type='nitf'", function(it) {
        //    console.log('getFeature', it);
        //} );

        //
        //wfsClient.getFeatureTypes( function ( w )
        //{
        //    console.log( '2', w.name );
        //} );
        //
        //console.log( '3', wfsClient.getFeatureTypes().map( function ( it )
        //{
        //    return it.name;
        //} ) );
        //
        //var featureTypeName = 'topp:states';
        //wfsClient.getFeatureTypeSchema( featureTypeName );
        //wfsClient.getFeature( featureTypeName, "STATE_ABBR='IN'" );
        //
        //var getFeatureURL = "http://localhost:8080/geoserver/wfs";
//        var getFeatureURL = "http://clc.developpement-durable.gouv.fr/geoserver/wfs";
    }

    return {
        init: init
    }
})();
