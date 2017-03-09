 <div id = "imagePropertiesDiv">
	<div>
        <div align = "right">
            <button class = "btn btn-default btn-sm" onclick = 'javascript:$( "#imagePropertiesDiv" ).hide()' type = "button">
                <span class = "glyphicon glyphicon-remove"></span>
            </button>
        </div>
        <table class = "table">
            <tr>
                <td>Bands:</td>
                <td>
                    <select class = "form-control" id = "selectBandsMethodSelect" onchange = "selectBands( this.value )">
                        <g:each in = "${[ "default", "manual" ]}">
                            <option value = ${ it }>${ it.capitalize() }</option>
                        </g:each>
                    </select>
                    <table id = "manualBandSelectTable" style = "background: none; display: none">
                        <tr align = "center">
                            <td>R</td>
                            <td>G</td>
                            <td>B</td>
                        </tr>
                        <tr>
                            <g:each in = "${[ "red", "green", "blue" ]}">
                                <td>
                                    <select class = "form-control" id = "${ it }GunSelect" onchange = "updateImageProperties( true )"></select>
                                </td>
                            </g:each>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td>Brightness: <span id = "brightnessValueSpan"></span></td>
                <td>
                    <input data-slider-id = "brightnessSlider" id = "brightnessSliderInput" type = "text"/>
                </td>
            </tr>
            <tr>
                <td>Contrast: <span id = "contrastValueSpan"></span></td>
                <td>
                    <input data-slider-id = "contrastSlider" id = "contrastSliderInput" type = "text"/>
                </td>
            </tr>
            <tr>
                <td>DRA:</td>
                <td>
                    <select class = "form-control" id = "dynamicRangeSelect" onchange = "updateImageProperties( true )">
                        <g:each in = "${[
                            [ name: "None", value: "none" ],
                            [ name: "Auto", value: "auto-minmax" ],
                            [ name: "STD 1", value: "std-stretch-1" ],
                            [ name: "STD 2", value: "std-stretch-2" ],
                            [ name: "STD 3", value: "std-stretch-3" ]
                        ]}">
                            <option value = ${ it.value }>${ it.name }</option>
                        </g:each>
                    </select>
                </td>
            </tr>
            <tr>
                <td>DRA Region:</td>
                <td>
                    <select class = "form-control" id = "dynamicRangeRegionSelect" onchange = "updateImageProperties( true )">
                        <g:each in = "${[
                            [ name: "Global", value: "false" ],
                            [ name: "Viewport", value: "true" ]
                        ]}">
                            <option value = ${ it.value }>${ it.name }</option>
                        </g:each>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Interpolation:</td>
                <td>
                    <select class = "form-control" id = "interpolationSelect" onchange = "updateImageProperties( true )">
                        <g:each in = "${[
                            "bessel",
                            "bilinear",
                            "blackman",
                            "bspline",
                            "catrom",
                            "cubic",
                            "gaussian",
                            "hamming",
                            "hermite",
                            "lanczos",
                            "magic",
                            "mitchell",
                            "nearest",
                            "quadratic",
                            "sinc"
                        ]}">
                            <option value = ${ it }>${ it.capitalize() }</option>
                        </g:each>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Opacity: <span id = "opacityValueSpan"></span></td>
                <td>
                    <input data-slider-id = "opacitySlider" id = "opacitySliderInput" type = "text"/>
                </td>
            </tr>
            <tr>
                <td>Sharpen Mode:</td>
                <td>
                    <select class = "form-control" id = "sharpenModeSelect" onchange = "updateImageProperties( true )">
                        <g:each in = "${[ "none", "light", "heavy" ]}">
                            <option value = ${ it }>${ it.capitalize() }</option>
                        </g:each>
                    </select>
                </td>
        </table>
	</div>
</div>
