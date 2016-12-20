<li class = "dropdown">
	<a href ="javascript:void(0)" class = "dropdown-toggle" data-toggle = "dropdown" role = "button">
		Annotations <span class="caret"></span>
	</a>
	<ul class = "dropdown-menu">
		<li><a href = javascript:void(0) onclick = "drawAnnotation('circle'); $('.navbar-collapse').collapse('hide');">Circle</a></li>
		<li><a href = javascript:void(0) onclick = "drawAnnotation('lineString'); $('.navbar-collapse').collapse('hide');">Line</a></li>
		<li><a href = javascript:void(0) onclick = "drawAnnotation('point'); $('.navbar-collapse').collapse('hide');">Point</a></li>
		<li><a href = javascript:void(0) onclick = "drawAnnotation('polygon'); $('.navbar-collapse').collapse('hide');">Polygon</a></li>
		<li><a href = javascript:void(0) onclick = "drawAnnotation('rectangle'); $('.navbar-collapse').collapse('hide');">Rectangle</a></li>
		<li><a href = javascript:void(0) onclick = "drawAnnotation('square'); $('.navbar-collapse').collapse('hide');">Square</a></li>
		<li class = "divider" role = "seperator"</li>
		<li><a href = javascript:void(0) onclick = "modifyAnnotations(); $('.navbar-collapse').collapse('hide');">Modify</a></li>
		<%--<li><a href = javascript:void(0) onclick = "deleteAnnotations(); $('.navbar-collapse').collapse('hide');">Delete</a></li>--%>
	</ul>
</li>
