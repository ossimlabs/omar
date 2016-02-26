<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 11/13/15
  Time: 1:43 PM
--%>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <title>OssimTools</title>
    <asset:stylesheet src="ossimtools.css"/>
</head>

<body>

<div class="container">
    <br/>
    <div class="row alert alert-success">
        <div class="col-md-12">
            <h1>OSSIM | Tools</h1>
        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="col-md-8">
            <div id="map"></div>
        </div>

        <div class="col-md-4">
            <form>
            
<%-- Common --%>
                 <div class="row">
                     <div class="col-md-12">
                        <span id="helpBlock" class="help-block">Map Center Coordinates</span>
                    </div>
                 </div>
                 <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="lat">Latitude</label>
                            <input type="text" class="form-control" id="lat" placeholder="28.57">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="lon">Longitude</label>
                            <input type="text" class="form-control" id="lon" placeholder="-81.24">
                        </div>
                    </div>
                   <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="radiusROI">Radius of Interest</label>
                            <input type="text" class="form-control" id="radiusROI" placeholder="0">
                        </div>
                    </div>
                 </div>


<%-- HLZ --%>
                <div class="row">
                    <div class="col-md-6">
                        <div class="checkbox">
                            <label>
                                <input id="toggleHLZ" type="checkbox" unchecked>HLZ
                            </label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="radiusLZ">LZ Radius</label>
                            <input type="text" class="form-control" id="radiusLZ" placeholder="0">
                        </div>
                    </div>
                     <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="slope">Max Inclination</label>
                            <input type="text" class="form-control" id="slope" placeholder="0">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="roughness">Max Roughness</label>
                            <input type="text" class="form-control" id="roughness" placeholder="0">
                        </div>
                    </div>
                </div>

<%-- Viewshed --%>
                <div class="row">
                    <div class="col-md-6">
                        <div class="checkbox">
                            <label>
                                <input id="toggleViewshed" type="checkbox" unchecked>Viewshed
                            </label>
                        </div>
                    </div>
                </div>
                
               <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="fovStart">FOV Start</label>
                            <input type="text" class="form-control" id="fovStart" placeholder="0">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="fovStop">FOV Stop</label>
                            <input type="text" class="form-control" id="fovStop" placeholder="0">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="heightOfEye">Height of Eye</label>
                            <input type="text" class="form-control" id="heightOfEye" placeholder="0">
                        </div>
                    </div>
                </div>

<%-- Slope --%>
                <div class="row">
                    <div class="col-md-6">
                        <div class="checkbox">
                            <label>
                                <input id="toggleSlope" type="checkbox" unchecked>Terrain Slope
                            </label>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="gainFactor">Gain Factor</label>
                            <input type="text" class="form-control" id="gainFactor" placeholder="2">
                        </div>
                    </div>
                </div>
 

<%-- Hillshade --%>
                <div class="row">
                    <div class="col-md-6">
                        <div class="checkbox">
                            <label>
                                <input id="toggleHillshade" type="checkbox" unchecked>Hillshade
                            </label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="sunEl">Sun Elevation</label>
                            <input type="text" class="form-control" id="sunEl" placeholder="45">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="label label-primary" for="sunAz">Sun Azimuth</label>
                            <input type="text" class="form-control" id="sunAz" placeholder="135">
                        </div>
                    </div>
                </div>

<%-- Submit --%>
                <div class="row">
                    <div class="col-md-12">
                        <hr/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <button type="button" class="btn btn-success" id="submitButton">Submit</button>
                        </div>
                    </div>
                </div>
                
            </form>
        </div>
    </div>
</div>
<asset:javascript src="ossimtools.js"/>
<asset:script>
    $(document).ready(function() {
        var initParmas = ${raw( initParams.encodeAsJSON() as String )};
        ossimtools.initialize(initParmas);
    });
</asset:script>
<asset:deferredScripts/>
</body>
</html>
